
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class KMedians { //implements kmedians++ algorithm
    private static final int MAX_COUNT = 10;
    private int K;
    private List<Vector> samples;
    private Map<Vector, Set<Vector>> clusters;
    private Map<Vector, Set<Vector>> bestSolution;
    private Double[][] distanceMatrix;
    private int[] centroidIndices;
    private int index;
    private boolean[] hasBeenChosenBefore;
    private Calculator calculator;

    public KMedians(int K, List<Vector> samples) {
        this.K = K;
        this.samples = samples;
        this.clusters = new LinkedHashMap<>(); //saves insertion order
        this.bestSolution = new HashMap<>();
        this.calculator = new Calculator();
        this.distanceMatrix = new Double[samples.size()][samples.size()];
        for (int i = 0; i < samples.size(); i++) {
            Arrays.fill(distanceMatrix[i], null);
        }
        this.centroidIndices = new int[K];
        this.index = 0;
        this.hasBeenChosenBefore = new boolean[samples.size()];
        Arrays.fill(this.hasBeenChosenBefore, false);
    }

    public Map<Vector, Set<Vector>> getSolution() {
        return this.bestSolution;
    }

    public void solve() throws IOException {
        long beginning = System.currentTimeMillis();
        int randomRestartCounter = 0;
        while (randomRestartCounter < MAX_COUNT) {
            this.index = 0;
            this.clusters = findSolution();
            if (withinPointScatter(this.clusters) < withinPointScatter(this.bestSolution)) {
                this.bestSolution = this.clusters;
            }
            randomRestartCounter++;
        }
        long end = System.currentTimeMillis();
        System.out.println("Computation time: " + (end - beginning) + "ms");
    }

    public void showSolution(Set<List<Integer>> testData, int testCount) {
//        for (Vector key : bestSolution.keySet()) {
//            System.out.print("***");
//            Set<Vector> value = bestSolution.get(key);
//            for(Vector v : value) {
//                System.out.print(v.id+",");
//            }
//            System.out.println("***");
//        }
        System.out.println( "Accuracy: " + calculator.calculateAccuracy(testData, this.bestSolution, testCount) + "%");
    }

    private Map<Vector, Set<Vector>> findSolution() {
        boolean isSolved = false;

//        initRandomCentroids();
        initSmartCentroids();
        initClusters();

        while (!isSolved) {
            Set<Vector> pastCentroids = clusters.keySet();

            updateCentroids();
            updateClusters();

            if (pastCentroids.equals(clusters.keySet())) {
                isSolved = true;
            }
        }
        return this.clusters;
    }

    public double withinPointScatter(Map<Vector, Set<Vector>> clusters) {
        if (clusters.size() == 0) {
            return Double.MAX_VALUE;
        }
        return clusters.keySet().stream().mapToDouble(p -> Math.pow(withinPointScatter(p, clusters.get(p)), 2)).sum(); //не трябва ли да е на втора?
    }

    private void initSmartCentroids() {
        clusters = new HashMap<>();
        int centroidIndex;
        do {
            centroidIndex = ThreadLocalRandom.current().nextInt(0, samples.size());
        } while (hasBeenChosenBefore[centroidIndex] == true);
        hasBeenChosenBefore[centroidIndex] = true;
        clusters.put(samples.get(centroidIndex), new HashSet<>());

        centroidIndices[index] = centroidIndex;
        index++;

        while (clusters.size() < K) {
            clusters.put(findNextCentroid(), new HashSet<>());
        }
    }

    private Vector findNextCentroid() {
        double biggestDistanceSquared = Double.MIN_VALUE;
        int indexOfNextCentroid = -1;

        for (int i = 0; i < samples.size(); i++) {
            if (!isAlreadyCentroid(i)) {
                int indexOfNearestCentroid = findClosestCentroid(i);

                double distanceSquared = Math.pow(distanceMatrix[i][indexOfNearestCentroid], 2);
                if (distanceSquared >= biggestDistanceSquared) {
                    biggestDistanceSquared = distanceSquared;
                    indexOfNextCentroid = i;
                }

            }
        }
        centroidIndices[index] = indexOfNextCentroid;
        index++;

        return samples.get(indexOfNextCentroid);
    }

    private int findClosestCentroid(int indexOfCurrentPoint) {
        Vector currentPoint = samples.get(indexOfCurrentPoint);

        double smallestDistance = Double.MAX_VALUE;
        int indexOfClosestCentroid = -1;

        for (int i = 0; i < index; i++) {
            Vector currentCentroid = samples.get(i);

            if (distanceMatrix[i][indexOfCurrentPoint] == null) {
                distanceMatrix[i][indexOfCurrentPoint] = calculator.calculateManhattanDistance(currentCentroid, currentPoint);
            }

            if (distanceMatrix[indexOfCurrentPoint][i] == null) {
                distanceMatrix[indexOfCurrentPoint][i] = calculator.calculateManhattanDistance(currentCentroid, currentPoint);
            }

            double currentDistance = distanceMatrix[i][indexOfCurrentPoint];
            if (currentDistance <= smallestDistance) {
                smallestDistance = currentDistance;
                indexOfClosestCentroid = i;
            }
        }
        return indexOfClosestCentroid;
    }

    private boolean isAlreadyCentroid(int i) {
        for (int j = 0; j < index; j++) {
            if (centroidIndices[j] == i) {
                return true;
            }
        }
        return false;
    }

    private void initRandomCentroids() {
        clusters = new HashMap<>();
        boolean takenNumbers[] = new boolean[samples.size()];
        for (int i = 1; i <= K; i++) {
            int randomCentroid;
            do {
                randomCentroid = ThreadLocalRandom.current().nextInt(0, samples.size());
            } while (takenNumbers[randomCentroid] == true);
            Vector centroid = samples.get(randomCentroid);
            clusters.put(centroid, new HashSet<>());
        }
    }

    private void updateCentroids() {
        Map<Vector, Set<Vector>> newClusters = new HashMap<>();
        for (Vector centroid : clusters.keySet()) {
            Set<Vector> cluster = clusters.get(centroid);
            Vector realCenter = calculateRealCenter(centroid, cluster);
            newClusters.put(realCenter, new HashSet<>());
        }
        this.clusters = newClusters;
    }

    private void updateClusters() {
        initClusters();
    }

    private void initClusters() {
        for (Vector v : samples) {
            Vector owner = getOwner(v);
            clusters.get(owner).add(v);
        }
    }

    private double withinPointScatter(Vector centroid, Set<Vector> cluster) {
        return cluster.stream().mapToDouble(p -> calculator.calculateManhattanDistance(p, centroid)).sum();
    }

    private Vector getOwner(Vector p) {
        double leastDistance = Double.MAX_VALUE;
        Vector centroidOfOwner = null;
        for (Vector centroid : clusters.keySet()) {
            double currentDistance = calculator.calculateManhattanDistance(p, centroid);
            if (currentDistance < leastDistance) {
                leastDistance = currentDistance;
                centroidOfOwner = centroid;
            }
        }
        return centroidOfOwner;
    }

    private Vector calculateRealCenter(Vector centroid, Set<Vector> cluster) {
        List<Double> coordinatesOfNewCenter = new ArrayList<>();
        int Nk = centroid.getCoordinates().size();
        for (int i = 0; i < Nk; i++) {
            coordinatesOfNewCenter.add(findMedianOfCoordinate(cluster, i));
        }
        return new Vector(coordinatesOfNewCenter);
    }

    private double findMedianOfCoordinate(Set<Vector> cluster, int coordinate) {
        List<Double> orderedCoordinates = new ArrayList<>();
        cluster.stream().forEach(vector -> orderedCoordinates.add(vector.getCoordinates().get(coordinate)));
        Collections.sort(orderedCoordinates);
        int Nk = orderedCoordinates.size();

        if (Nk % 2 == 0) {
            return Double.valueOf((orderedCoordinates.get(Nk / 2) + orderedCoordinates.get(Nk / 2 - 1))) / 2;
        }
        return orderedCoordinates.get(Nk / 2);
    }
}