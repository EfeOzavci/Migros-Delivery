import java.awt.*;
import java.util.Arrays;

public class AntColonyOptimizer {

    // define variables and constants.
    private final double[][] coordinates;
    private final int N;
    private final int M;
    private final double degradationFactor;
    private final double alpha;
    private final double beta;
    private double initialPheromoneIntensity;
    private final double Q;
    private double[][] pheromoneLevels;
    private double[][] distances;
    private double bestDistance;
    private int[] bestPath;

    public AntColonyOptimizer(double[][] coordinates, int N, int M, double degradationFactor,
                              double alpha, double beta, double initialPheromoneIntensity, double Q) {
        this.coordinates = coordinates;
        this.N = N;
        this.M = M;
        this.degradationFactor = degradationFactor;
        this.alpha = alpha;
        this.beta = beta;
        this.initialPheromoneIntensity = initialPheromoneIntensity;
        this.Q = Q;
        this.pheromoneLevels = new double[coordinates.length][coordinates.length];
        this.distances = calculateDistances(coordinates);
        this.bestDistance = Double.POSITIVE_INFINITY;
        this.bestPath = new int[coordinates.length+1];
        initializePheromoneLevels();
    }

    public void run() {
        for (int i = 0; i < N; i++) {
            // Initialize ants
            Ant[] ants = new Ant[M];
            for (int j = 0; j < M; j++) {
                ants[j] = new Ant(coordinates.length);
            }

            // Ant traversal
            for (Ant ant : ants) {
                ant.traverse(this);
            }

            // Update pheromone levels
            updatePheromoneLevels(ants);

            // Update best path
            updateBestPath(ants);

            // Degrade pheromone levels
            degradePheromoneLevels();
        }
    }

    private void initializePheromoneLevels() {
        for (int i = 0; i < pheromoneLevels.length; i++) {
            Arrays.fill(pheromoneLevels[i], initialPheromoneIntensity);
        }
    }

    private void updatePheromoneLevels(Ant[] ants) {
        for (Ant ant : ants) {
            int[] path = ant.getPath();
            double pathDistance = calculatePathDistance(path);
            double delta = Q / pathDistance;
            for (int i = 0; i < path.length - 1; i++) {
                int fromNode = path[i];
                int toNode = path[i + 1];
                pheromoneLevels[fromNode][toNode] += delta;
                pheromoneLevels[toNode][fromNode] += delta; // Assume symmetric pheromone update
            }
        }
    }

    private void degradePheromoneLevels() {
        for (int i = 0; i < pheromoneLevels.length; i++) {
            for (int j = 0; j < pheromoneLevels[i].length; j++) {
                pheromoneLevels[i][j] *= degradationFactor;
            }
        }
    }

    private void updateBestPath(Ant[] ants) {
        for (Ant ant : ants) {
            double pathDistance = calculatePathDistance(ant.getPath());
            if (pathDistance < bestDistance) {
                bestDistance = pathDistance;
                bestPath = ant.getPath();
            }
        }
    }

    private double calculatePathDistance(int[] path) {
        double distance = 0;
        for (int i = 0; i < path.length - 1; i++) {
            int fromNode = path[i];
            int toNode = path[i + 1];
            distance += distances[fromNode][toNode];
        }
        return distance;
    }

    private double[][] calculateDistances(double[][] coordinates) {
        double[][] distances = new double[coordinates.length][coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates.length; j++) {
                distances[i][j] = calculateDistance(coordinates[i], coordinates[j]);
            }
        }
        return distances;
    }

    private double calculateDistance(double[] coord1, double[] coord2) {
        double dx = coord1[0] - coord2[0];
        double dy = coord1[1] - coord2[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getPheromoneLevel(int fromNode, int toNode) {
        return pheromoneLevels[fromNode][toNode];
    }

    public double getDistance(int fromNode, int toNode) {
        return distances[fromNode][toNode];
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }



    public void plotPheromoneIntensityGraphs() {

        StdDraw.clear();
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);

        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates.length; j++) {
                double pheromoneLevel = pheromoneLevels[i][j];
                StdDraw.setPenRadius(pheromoneLevel/5);
                StdDraw.line(coordinates[i][0], coordinates[i][1], coordinates[j][0], coordinates[j][1]);}}

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        StdDraw.filledCircle(coordinates[0][0], coordinates[0][1], 0.02);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Serif", Font.BOLD, 14));
        StdDraw.text(coordinates[0][0], coordinates[0][1], Integer.toString(1));

        for (int k = 1; k < coordinates.length; k++) {
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(coordinates[k][0], coordinates[k][1], 0.02);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Serif", Font.BOLD, 14));
            StdDraw.text(coordinates[k][0], coordinates[k][1], Integer.toString(k+1));
        }

        StdDraw.show();
    }


    public void plotBestPath() {
        StdDraw.clear();
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        // Plot best path
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < bestPath.length-1; i++) {
            int fromNode = bestPath[i];
            int toNode = bestPath[i + 1];
            StdDraw.line(coordinates[fromNode][0], coordinates[fromNode][1],
                    coordinates[toNode][0], coordinates[toNode][1]);

            // Connect last node to the starting node
            int lastNode = bestPath[bestPath.length - 1];
            int startNode = bestPath[0];
            StdDraw.line(coordinates[lastNode][0], coordinates[lastNode][1],
                    coordinates[startNode][0], coordinates[startNode][1]);
        }
        // Plot nodes

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        StdDraw.filledCircle(coordinates[0][0], coordinates[0][1], 0.02);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Serif", Font.BOLD, 14));
        StdDraw.text(coordinates[0][0], coordinates[0][1], Integer.toString(1));

        for (int i = 1; i < coordinates.length; i++) {
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(coordinates[i][0], coordinates[i][1], 0.02);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Serif", Font.BOLD, 14));
            StdDraw.text(coordinates[i][0], coordinates[i][1], Integer.toString(i+1));
        }

        StdDraw.show();
    }

    public int[] getBestPath() {
        return Arrays.copyOf(bestPath, bestPath.length);
    }

    public double getBestDistance(){
        return bestDistance;
    }

}

