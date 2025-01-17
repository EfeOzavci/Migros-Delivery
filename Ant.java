import java.util.Arrays;
import java.util.Random;

public class Ant {
    private final int numNodes;
    private int[] path;
    private boolean[] visited;


    public Ant(int numNodes) {
        this.numNodes = numNodes;
        this.path = new int[numNodes+1];
        this.visited = new boolean[numNodes];
        Arrays.fill(visited, false);
    }

    public void traverse(AntColonyOptimizer optimizer) {
        path[0] = 0;
        visited[0] = true;

        // Traverse the rest of the nodes
        for (int i = 1; i < numNodes; i++) {
            int nextNode = selectNextNode(optimizer, path[i - 1]);
            path[i] = nextNode;
            visited[nextNode] = true;
        }

        // Complete the cycle by returning to the start node
        path[numNodes] = 0;
    }

    private int selectNextNode(AntColonyOptimizer optimizer, int currentNode) {
        double[] probabilities = new double[numNodes];
        double sum = 0.0;

        // Calculate probabilities for each possible next node
        for (int i = 0; i < numNodes; i++) {
            if (!visited[i]) {
                probabilities[i] = calculateProbability(optimizer, currentNode, i);
                sum += probabilities[i];
            }
        }

        // Normalize probabilities
        for (int i = 0; i < numNodes; i++) {
            probabilities[i] /= sum;
        }

        // Select the next node probabilistically
        double randomValue = Math.random();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < numNodes; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return i;
            }
        }

        // This should never happen
        return -1;
    }

    private double calculateProbability(AntColonyOptimizer optimizer, int currentNode, int nextNode) {
        double pheromoneIntensity = optimizer.getPheromoneLevel(currentNode, nextNode);
        double distance = optimizer.getDistance(currentNode, nextNode);
        return Math.pow(pheromoneIntensity, optimizer.getAlpha()) * Math.pow(1.0 / distance, optimizer.getBeta());
    }

    public int[] getPath() {
        return path;
    }


}
