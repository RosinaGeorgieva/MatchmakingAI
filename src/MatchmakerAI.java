import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MatchmakerAI implements Runnable {
    private static final String MAIN_DATA = "D:\\Desktop\\datasets\\data.csv";
    private static final String NAMES = "D:\\Desktop\\datasets\\names.csv";

    private static Map<Vector, Set<Vector>> solution = new HashMap<>();
    private static final Map<Integer, String> nameById = new HashMap<>();
    private static boolean isReady = false;

    @Override
    public void run() {
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
        System.out.println("You can try matching: " + names.get(ThreadLocalRandom.current().nextInt(0, names.size())));

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

        solution = problem.getSolution();

        synchronized (this) {
            this.notify();
        }

        this.isReady = true;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public static String getMatchFor(String user) throws IOException {
        int suggestedUserId = -1;
        for(Integer id : nameById.keySet()) {
            if(nameById.get(id).equals(user)) {
                suggestedUserId = id;
            }
        }

        String suggestedUser = null;
        for (Vector centroid : solution.keySet()) {
            Set<Vector> vectorsInCluster = solution.get(centroid);
            for (Vector vectorInCluster : vectorsInCluster) {
                if (vectorInCluster.id == suggestedUserId) {
                    List<Integer> otherUsersNames = vectorsInCluster.stream().map(vector -> vector.id).collect(Collectors.toList());
                    int randomUser;
                    do {
                       randomUser = ThreadLocalRandom.current().nextInt(0, otherUsersNames.size() - 1);
                    } while (randomUser == vectorInCluster.id);
                    suggestedUser = nameById.get(randomUser);
                    break;
                }
            }
        }

        if (suggestedUser == null) {
            return "No matches found!";
        }

//        saveMatchToFile(user, suggestedUser, solution);

        return suggestedUser;
    }

//    private static void saveMatchToFile(String firstUser, String secondUser, Map<Vector, Set<Vector>> solution) throws IOException {
//        String outputMessage = "Description of " + firstUser +  "'s personality: \n";
//        outputMessage += allAnswersDecription(userData.getIntCoordinates());
//        outputMessage += "\n";
//
//        List<String> otherUsersNames = otherUsersInThisCluster.keySet().stream().collect(Collectors.toList());
//        int randomUser = ThreadLocalRandom.current().nextInt(0, otherUsersInThisCluster.size() - 1);
//        String suggestedUser = otherUsersNames.get(randomUser);
//
//        outputMessage += "\n";
//        outputMessage += "Description of " + suggestedUser + "'s personality: \n";
//        outputMessage += allAnswersDecription(otherUsersInThisCluster.get(suggestedUser).getIntCoordinates());
//
//        Path compatibilityFile = Path.of("output\\"+ firstUser + "-" + suggestedUser + ".txt");
//        Files.createFile(compatibilityFile);
//
//        try (var printWriter = new PrintStream(new FileOutputStream(compatibilityFile.toString(), false))) {
//            printWriter.println(outputMessage);
//        }
//    }

//    private static String allAnswersDecription(List<Integer> answers) {
//        String description = "";
//        for (int i = 0; i < answers.size(); i++) {
//            description += codebook.get(i);
//            description += "->";
//            switch (answers.get(i)) {
//                case 1:
//                    description += "inaccurate";
//                    break;
//                case 2:
//                    description += "inaccurate to neutral";
//                    break;
//                case 3:
//                    description += "neutral";
//                    break;
//                case 4:
//                    description += "neutral to accurate";
//                    break;
//                case 5:
//                    description += "accurate";
//                    break;
//            }
//            description += '\n';
//        }
//        return description;
//    }
}
