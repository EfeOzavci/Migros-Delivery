import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Efe Özavcı
 * @version 1.0
 * @MigrosDelivery This program finds the quickest delivery route of a Migros delivery car.
 * There is a Migros store, denoted by the orange circle, and several houses, denoted by the gray circles.
 * Each house places an order from Migros. The delivery car visits each house only once and return to Migros.
 * The user can prefer two solution methods. The Brute-Force method and the Ant Colony Optimization method.
 * For the purpose of the program, different classes and methods are defined.
 */


public class Main {

    public static void main(String[] args) throws IOException {

        int chosenMethod = 1; // uses the brute-force method if set to 1, uses the ant colony optimization method if set to 2.

        int chosenGraph = 1; // plots the pheromone intensity graph if set to 1, plots the best path if set to 2.

        if (chosenMethod == 1) { // uses brute-force method.

            long startTime = System.currentTimeMillis(); // start time to denote the time it takes at the end.
            ArrayList<ArrayList<Double>> locations = new ArrayList<>(); // locations is an arraylist that holds the coordinates of the migros and the houses.
            int migrosIdx = 0;  // index of the migros.

            try { // here we read the file and put the coordinates in the locations arraylist.
                File file = new File("src/input01.txt");
                Scanner scanner = new Scanner(file);
                int idx = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    ArrayList<Double> coordinates = new ArrayList<>();
                    coordinates.add(Double.parseDouble(parts[0]));
                    coordinates.add(Double.parseDouble(parts[1]));
                    locations.add(coordinates);

                    idx++;
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while reading the file.");
                e.printStackTrace();
                return;
            }

            // here we set the initial route
            Integer[] initialRoute = new Integer[locations.size() - 1];
            int index = 0;
            for (int i = 0; i < locations.size(); i++) {
                if (i != migrosIdx) {
                    initialRoute[index++] = i;
                }
            }

            double[] minDistance = {Double.MAX_VALUE}; // set teb minDistance to a max value.
            Integer[] bestRoute = new Integer[initialRoute.length + 2]; // define the best route
            for (int i = 0; i < initialRoute.length + 2; i++) {
                bestRoute[i] = 0;
            }

            permute(initialRoute, 0, locations, migrosIdx, minDistance, bestRoute); // make the permutations and change the best route accordingly.


            String bestRouteText = Arrays.toString(Arrays.stream(bestRoute).map(i -> i + 1).toArray(Integer[]::new)); // make the best route a string.
            System.out.println("Method: Brute-Force Method");
            System.out.println("Shortest Distance: " + minDistance[0]);
            System.out.println("Shortest Path: " + bestRouteText);


            // set the canvas attributes.
            StdDraw.clear();
            int canvasWidth = 800;
            int canvasHeight = 800;
            StdDraw.setCanvasSize(canvasWidth, canvasHeight);
            StdDraw.setXscale(0, 1);
            StdDraw.setYscale(0, 1);

            // draw the lines.
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.005);
            ArrayList<Double> firstLocation = locations.get(migrosIdx);
            double locationX = firstLocation.get(0);
            double locationY = firstLocation.get(1);
            for (int idx : bestRoute) {
                ArrayList<Double> nextLocation = locations.get(idx);
                double nextLocationX = nextLocation.get(0);
                double nextLocationY = nextLocation.get(1);
                StdDraw.line(locationX, locationY, nextLocationX, nextLocationY);
                locationX = nextLocationX;
                locationY = nextLocationY;
            }

            // draw the circles and write the numbers on top them.
            for (int i = 0; i < locations.size(); i++) {
                ArrayList<Double> location = locations.get(i);
                if (i == migrosIdx) {
                    StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                } else {
                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                }
                StdDraw.filledCircle(location.get(0), location.get(1), 0.02);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.setFont(new Font("Serif", Font.BOLD, 14));
                StdDraw.text(location.get(0), location.get(1), Integer.toString(i + 1));
            }
            StdDraw.show();
            //STDRAW---------------------------------------------------------------------------------------------------
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime); // calculate duration.
            System.out.println("Time it takes to find the shortest path: " + duration / 1000.0 + " seconds.");

        } else if (chosenMethod == 2) { // uses ant colony optimization method.

            long startTime = System.currentTimeMillis(); // start time to denote the time it takes at the end.

            // Define parameters.
            int N = 300; // Iteration count.
            int M = 200; // Ant count per iteration.
            double degradationFactor = 0.9;
            double alpha = 0.8; // gives priority to pheromone intensity.
            double beta = 1.5; // gives priority to edge distance.
            double initialPheromoneIntensity = 0.1;
            double Q = 0.0001;

            // Read input file
            double[][] coordinates = readCoordinatesFromFile("src/input01.txt"); // read coordinates from the file.

            // Implement ant colony optimization algorithm
            AntColonyOptimizer optimizer = new AntColonyOptimizer(coordinates, N, M, degradationFactor, alpha, beta, initialPheromoneIntensity, Q); // create a new optimizer.
            optimizer.run(); // use the run method to initialize ants and use other methods.

            if (chosenGraph == 1) {
                optimizer.plotPheromoneIntensityGraphs(); // plot pheromone intensity graph if chosen.
            } else if (chosenGraph == 2) {
                optimizer.plotBestPath(); // plot the best path if chosen.
            }


            System.out.println("Method: Ant Colony Optimization Method");

            double shortestDistance = optimizer.getBestDistance(); // get the shortest distance.

            System.out.print("Shortest Distance: ");
            System.out.println(shortestDistance);

            int[] bestPath = optimizer.getBestPath(); // get the best path.
            System.out.print("Shortest Path: [");

            for (int i = 0; i < bestPath.length; i++) {
                if (i == bestPath.length - 1) {
                    System.out.print(bestPath[i] + 1);
                    System.out.println("]");
                } else {
                    System.out.print(bestPath[i] + 1 + ", ");
                }
            }

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime); // calculate the duraiton.
            System.out.println("Time it takes to find the shortest path: " + duration / 1000.0 + " seconds.");

        }
    }

    // make the permutation and update the min distance if needed.
    public static void permute(Integer[] arr, int k, ArrayList<ArrayList<Double>> locations, int migrosIdx, double[] minDistance, Integer[] bestRoute) {
        if (k == arr.length) {
            double distance = calculateRouteDistance(arr, locations, migrosIdx);
            if (distance < minDistance[0]) {
                minDistance[0] = distance;
                for (int i = 1; i < arr.length + 1; i++) {
                    bestRoute[0] = 0;
                    bestRoute[i] = arr[i - 1];
                }
                bestRoute[arr.length + 1] = 0;

            }

        } else {
            for (int i = k; i < arr.length; i++) {
                Integer temp = arr[i];
                arr[i] = arr[k];
                arr[k] = temp;
                permute(arr, k + 1, locations, migrosIdx, minDistance, bestRoute);
                temp = arr[k];
                arr[k] = arr[i];
                arr[i] = temp;
            }
        }
    }

    // calculate the route distance.
    private static double calculateRouteDistance(Integer[] route, ArrayList<ArrayList<Double>> locations, int migrosIdx) {
        double distance = 0;
        int prevIdx = migrosIdx;
        for (int locationIdx : route) {
            distance += Math.sqrt(Math.pow(locations.get(locationIdx).get(0) - locations.get(prevIdx).get(0), 2) +
                    Math.pow(locations.get(locationIdx).get(1) - locations.get(prevIdx).get(1), 2));
            prevIdx = locationIdx;
        }
        distance += Math.sqrt(Math.pow(locations.get(migrosIdx).get(0) - locations.get(prevIdx).get(0), 2) +
                Math.pow(locations.get(migrosIdx).get(1) - locations.get(prevIdx).get(1), 2));
        return distance;
    }

    // read the coordinates from the file.
    private static double[][] readCoordinatesFromFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        // Skip the first line (Migros coordinate)
        String line = reader.readLine();
        double[] migrosCoord = parseCoordinateLine(line);


        // Count the number of houses
        int numHouses = 0;
        while ((line = reader.readLine()) != null) {
            numHouses++;
        }
        reader.close();

        // Initialize coordinates array with Migros + houses
        double[][] coordinates = new double[numHouses + 1][]; // Migros + houses
        coordinates[0] = migrosCoord;

        // Read and store house coordinates
        reader = new BufferedReader(new FileReader(fileName));
        reader.readLine(); // Skip the first line (already read)
        int houseIndex = 1;
        while ((line = reader.readLine()) != null) {
            coordinates[houseIndex++] = parseCoordinateLine(line);
        }
        reader.close();

        return coordinates;
    }


    private static double[] parseCoordinateLine(String line) {
        String[] parts = line.split(",");
        double[] coordinate = new double[2];
        coordinate[0] = Double.parseDouble(parts[0]);
        coordinate[1] = Double.parseDouble(parts[1]);
        return coordinate;
    }
}







