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

    public static void main(String... args) {
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

        int K = 10; //variates
        KMedians problem = new KMedians(K, observationVectors);
        try {
            problem.solve();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

//        solution = problem.getSolution();
//        System.out.println(solution);
        problem.showSolution(testData, 2500);


    }
}
