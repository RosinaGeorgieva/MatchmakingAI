import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Vector {
    private static final String COMMA = ",";

    public int id;
    public List<Double> vector;

    public Vector(String sample) { //очакваме че ще приемаме стринг с вектора в оригиналния му вид
        String[] coordinates = sample.split(COMMA);
        vector = new ArrayList<>();
        id = Integer.parseInt(coordinates[0]);
        Arrays.stream(Arrays.copyOfRange(coordinates, 3, coordinates.length)).mapToDouble(Integer::valueOf).forEachOrdered(vector::add);
    }

    public Vector(List<Double> vector) {
        this.vector = vector;
    }

    public List<Double> getCoordinates() {
        return this.vector;
    }

    public List<Integer> getIntCoordinates() {
        List<Integer> intCoordinates = new ArrayList<>();
        for(Double coord : vector) {
            intCoordinates.add((int)coord.doubleValue());
        }
        return intCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector other = (Vector) o;
        return this.vector.equals(other.getCoordinates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(vector);
    }

    @Override
    public String toString() {
        List<Integer> preferences = new ArrayList<>();
        for(Double coordinate : vector) {
            preferences.add((int)coordinate.doubleValue());
        }
        return preferences.stream().map(String::valueOf).collect(Collectors.joining(COMMA));
    }

    public void print() {
        for(Double c : vector) {
            System.out.print(c + ",");
        }
        System.out.println();
    }
}
