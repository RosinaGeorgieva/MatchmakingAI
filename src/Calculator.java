import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Calculator {
    public double calculateAccuracy(Set<List<Integer>> test, Map<Vector, Set<Vector>> solution, int iterCount) {
        int i = 0;
        List<List<Integer>> testList = new ArrayList<>(test);

        int correctCount = 0;
        int correctT = 0;
        int correctF = 0;
        while(i < iterCount) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, testList.size());
            List<Integer> obs = testList.get(randomIndex);
            int id = obs.get(0);
            int pid = obs.get(1);
            int match = obs.get(2);

            Vector keyOfId = null;
            Vector keyOfPid = null;
            for(Vector key : solution.keySet()) {
                Set<Vector> values = solution.get(key);
                for(Vector v : values) {
                    if(v.id == id) {
                        keyOfId = key;
                    }
                    if(v.id == pid) {
                        keyOfPid = key;
                    }
                }
            }

//            if(match == 0) {
//                correctF++;
//            }

            if((keyOfId.equals(keyOfPid) && match == 1) || (!keyOfId.equals(keyOfPid) && match == 0)) {
                correctCount++;
            }

            i++;
        }
        return (double) correctCount / iterCount;
    }

    public double calculateManhattanDistance(Vector firstVector, Vector secondVector) {
        double manhattanDist = 0;
        List<Double> firstVectorCoordinates = firstVector.getCoordinates();
        List<Double> secondVectorCoordinates = secondVector.getCoordinates();
        for (int i = 0; i < firstVectorCoordinates.size(); i++) {
            manhattanDist += Math.abs(firstVectorCoordinates.get(i) - secondVectorCoordinates.get(i));
        }

        return manhattanDist;
    }

    public double calculateAverageSilhouetteCoefficient(Map<Vector, Set<Vector>> clusters) {
        if (clusters.keySet().size() == 1) {
            return 0.0;
        }

        double sum = 0.0;
        int sampleSize = 0;
        for (Vector centroid : clusters.keySet()) {
            Set<Vector> vectors = clusters.get(centroid);
            for (Vector vi : vectors) {
                sum += calculateSilhouetteCoefficient(clusters, centroid, vi);
                sampleSize++;
            }
        }
        return sum / sampleSize;
    }

    private double calculateSilhouetteCoefficient(Map<Vector, Set<Vector>> clusters, Vector centroid, Vector current) {
        double x = meanDistanceToOthers(clusters.get(centroid), current);
        double y = meanNearestClusterDistance(clusters, centroid, current);

        return (x - y) / Math.max(x, y);
    }

    private double meanDistanceToOthers(Set<Vector> vectors, Vector current) {
        double distance = 0;
        for (Vector vj : vectors) {
            if (!current.equals(vj)) {
                distance += calculateManhattanDistance(current, vj);
            }
        }
        distance /= vectors.size() - 1;
        return distance;
    }

    //single linkage
    private double meanNearestClusterDistance(Map<Vector, Set<Vector>> clusters, Vector centroid, Vector current) {
        double min = Double.MAX_VALUE;
        Vector centroidOfClosestCluster = null;
        for (Vector otherCentroid : clusters.keySet()) {
            if (!centroid.equals(otherCentroid)) {
                Set<Vector> vectors = clusters.get(otherCentroid);
                for (Vector vector : vectors) {
                    double distance = calculateManhattanDistance(current, vector);
                    if (distance < min) {
                        min = distance;
                        centroidOfClosestCluster = otherCentroid;
                    }
                }
            }
        }
        double distance = 0.0;
        Set<Vector> closestCluster = clusters.get(centroidOfClosestCluster);
        for (Vector v : closestCluster) {
            distance += calculateManhattanDistance(current, v);
        }
        return distance / closestCluster.size();
    }
}
