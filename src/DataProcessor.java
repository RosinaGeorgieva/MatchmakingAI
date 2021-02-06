import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataProcessor {
    private static final String HELPER = "D:\\Desktop\\datasets\\helper.csv";
    private static final String EMPTY_SPACE = ",,";
    private static final String EMPTY_ANSWER = "NA";
    private static final String HEADER = "iid";
    private static final String HEADER_NAMES = "Name";
    private static final String COMMA = ",";
    private static final int NUMBER_OF_OBS = 336;

    public List<String> chooseNames(Path filePath) throws IOException {
        Stream<String> lines = Files.lines(filePath);

        Set<String> names = new HashSet<>();

        lines.filter(line -> !line.contains(HEADER_NAMES))
                .map(line -> line.split(COMMA)[1])
                .forEachOrdered(line -> names.add(line));

        return new ArrayList<>(names).subList(0, NUMBER_OF_OBS);
    }

    public Set<List<Integer>> chooseTestData(Path filePath) throws IOException {
        var reader = new BufferedReader(new FileReader(filePath.toString()));

        Set<List<Integer>> observations = new HashSet<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if(!line.contains(HEADER)) {
                String[] args = line.split(COMMA);
                Integer iid = Integer.parseInt(args[0]);
                Integer pid = Integer.parseInt(args[1]);
                Integer match = Integer.parseInt(args[2]);
                observations.add(List.of(iid, pid, match));
            }
        }

        reader.close();

        return observations;
    }

    public List<Vector> chooseObservations(Path filePath) throws IOException {
        var reader = new BufferedReader(new FileReader(filePath.toString()));

        List<Vector> observations = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if(!line.contains(HEADER)) {
                Vector fromThisLine = new Vector(line);
                if(!containsSuch(observations, fromThisLine)) {
                    observations.add(fromThisLine);
                }
            }
        }

        reader.close();

        return observations;
    }

    public void processData(Path filePath) throws IOException {
        var reader = new BufferedReader(new FileReader(filePath.toString()));

        Path fileWithoutEmptyAnswers = Path.of(HELPER);
        var printWriter = new PrintStream(new FileOutputStream(fileWithoutEmptyAnswers.toString(), false));

        int i = 0;
        String line;
        while ((line = reader.readLine()) != null && i < 5000) {
            while (line.contains(EMPTY_SPACE)) {
                line = replaceEmptyAnswers(line);
                line = removeDashes(line);
                line = removeQuotes(line);
            }

            String[] variables = line.split(COMMA);
            String id = variables[0];
            String pid = variables[11];
            String match = variables[12];
            String[] interests = Arrays.copyOfRange(variables, 51, 67);
            line = id + COMMA + pid + COMMA + match + COMMA + Arrays.stream(interests).collect(Collectors.joining(COMMA));

            if (!line.contains(EMPTY_ANSWER)) {
                printWriter.println(line);
                i++;
            }
        }

        printWriter.close();
        reader.close();

        Files.move(fileWithoutEmptyAnswers, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static String replaceEmptyAnswers(String string) {
        return string.replaceAll(EMPTY_SPACE, ",NA,");
    }

    private static String removeDashes(String string) {
        return string.replaceAll("-", "");
    }

    private static String removeQuotes(String string) {
        return string.replaceAll("\"[a-zA-Z0-9,. /()]+\"", "NA");
    }

    private boolean containsSuch(List<Vector> list, Vector vector) {
        for(Vector v : list) {
            if(v.id == vector.id) {
                return true;
            }
        }
        return false;
    }
}
