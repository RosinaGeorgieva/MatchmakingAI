import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
    private static final String MAIN_DATA = "D:\\Desktop\\datasets\\data.csv"; //дали да не го направя вместо по сх по личност, по интереси?

    private static Map<Vector, Set<Vector>> solution = new HashMap<>();

    public static void main(String... args) throws IOException {
        DataProcessor dp = new DataProcessor();
//        try {
//            dp.processData(Path.of(MAIN_DATA));
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }

        List<Vector> observationVectors = new ArrayList<>();
        Set<List<Integer>> testData = new HashSet<>();
        try {
            observationVectors = dp.chooseObservations(Path.of(MAIN_DATA));
            testData = dp.chooseTestData(Path.of(MAIN_DATA));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

//        //Elbow method: ambiguous
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

        //Silhouette method -> k = 10
//        List<Double> silhoetteCoefficient = new ArrayList<>();
//        Calculator calculator = new Calculator();
//        for(int K = 1; K <= 15; K++) {
//            KMedians problem = new KMedians(K, observationVectors);
//            problem.solve();
//            Double currentWss = calculator.calculateAverageSilhouetteCoefficient(problem.getSolution());
//            silhoetteCoefficient.add(currentWss);
//        }
//        LinePlot plot = new LinePlot(silhoetteCoefficient,
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
}
