import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class App {
    private static final String MAIN_DATA = "D:\\Desktop\\datasets\\data.csv";
    private static final String NAMES = "D:\\Desktop\\datasets\\names.csv";

    private static Map<Vector, Set<Vector>> solution = new HashMap<>();
    private static final Map<Integer, String> nameById = new HashMap<>();

    public static void main(String... args) {
        DataProcessor dp = new DataProcessor();
//        // Processes data file so that it leaves out only the necessary for the matchmaker
//        try {
//            dp.processData(Path.of(MAIN_DATA));
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }

        // Fills in data inside data structures
        List<Vector> observationVectors = new ArrayList<>();
        Set<List<Integer>> testData = new HashSet<>();
        List<String> names = new ArrayList<>();
        try {
            observationVectors = dp.chooseObservations(Path.of(MAIN_DATA));
            testData = dp.chooseTestData(Path.of(MAIN_DATA));
            names = dp.chooseNames(Path.of(NAMES));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        for (int i = 0; i < observationVectors.size(); i++) {
            nameById.put(observationVectors.get(i).id, names.get(i));
        }

        // Prints a random name for us to know in case we want to try and make a match
        System.out.println(names.get(ThreadLocalRandom.current().nextInt(0, names.size())));

//        // Elbow method: ambiguous
//        List<Double> wss = new ArrayList<>();
//        for(int K = 1; K <= 15; K++) {
//           KMedians problem = new KMedians(K, observationVectors);
//            problem.solve();
//            Double currentWss = problem.withinPointScatter(problem.getSolution());
//            wss.add(currentWss);
//        }
//        LinePlot plot = new LinePlot(wss, "WSS by number of clusters K \n (Elbow method)", "Total WSS");
//        plot.setSize(800, 400);
//        plot.setLocationRelativeTo(null);
//        plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        plot.setVisible(true);

        //Silhouette method: k = 8
//        List<Double> silhouetteCoefficient = new ArrayList<>();
//        Calculator calculator = new Calculator();
//        for(int K = 1; K <= 15; K++) {
//            KMedians problem = new KMedians(K, observationVectors);
//            problem.solve();
//            Double currentWss = calculator.calculateAverageSilhouetteCoefficient(problem.getSolution());
//            silhoetteCoefficient.add(currentWss);
//        }
//        LinePlot plot = new LinePlot(silhouetteCoefficient,
//                "Average silhouette coefficient by number of clusters K \n (Silhouette method)"
//                , "Silhouette coefficient");
//        plot.setSize(800, 400);
//        plot.setLocationRelativeTo(null);
//        plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        plot.setVisible(true);

        int K = 8;
        KMedians problem = new KMedians(K, observationVectors);
        try {
            problem.solve();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        problem.showSolution(testData, 2500);
    }

    private static void gui() {
        JFrame frame = new JFrame();
        frame.setTitle("Matchmaking recommender system");
        frame.setSize(400, 500);

        var inputField = new JTextField("Enter username...");
        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText("");
            }
        });
        inputField.setBounds(20, 20, 350, 30);
        frame.add(inputField);

        var button = new JButton("Matchmake!");
        button.setBounds(265, 60, 105, 20);
        frame.add(button);

        var console = new JTextArea("Results...");
        console.setBounds(20, 90, 350, 200);
        console.setEditable(false);
        frame.add(console);

        button.addActionListener((event) -> {
            console.setText("");
            console.append(getMatches(inputField.getText()));
        });

        frame.setLayout(null);
        frame.setVisible(true);
    }

    private static String getMatches(String user) {
        Vector centroidOfClusterOfThisUser = null;
        for (Vector centroid : solution.keySet()) {
            Set<Vector> vectorsInCluster = solution.get(centroid);
            for (Vector v : vectorsInCluster) {
                if (nameById.get(v).equals(user)) {
                    centroidOfClusterOfThisUser = centroid;
                    break;
                }
            }
        }

        if (centroidOfClusterOfThisUser == null) {
            return "No matches found!";
        }

        Map<String, Vector> otherUsersInThisCluster = new HashMap<>();
        for (Vector v : solution.get(centroidOfClusterOfThisUser)) {
            otherUsersInThisCluster.put(nameById.get(v), v);
        }

        List<String> otherUsersNames = otherUsersInThisCluster.keySet().stream().collect(Collectors.toList());
        int randomUser = ThreadLocalRandom.current().nextInt(0, otherUsersInThisCluster.size() - 1);
        return otherUsersNames.get(randomUser);
    }
}
