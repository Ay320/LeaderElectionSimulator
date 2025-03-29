import java.util.*;

/**
 * A simulator for leader election algorithms in a bidirectional ring network.
 * Supports LCR and HS algorithms with different ID assignments.
 */
public class LeaderElectionSimulator {
    /**
     * The main method that drives the simulation process. It prompts the user to specify
     * the run type (single or multiple), the algorithm (LCR or HS), and the ID assignment
     * type (ascending, descending, or random).
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int alpha = 3; // Constant for random ID generation range

        // Prompt for run type
        System.out.print("Enter run type (single, multiple): ");
        String runType = scanner.next().toLowerCase();
        while (!runType.equals("single") && !runType.equals("multiple")) {
            System.out.print("Invalid run type. Use 'single' or 'multiple': ");
            runType = scanner.next().toLowerCase();
        }

        // Prompt for algorithm choice
        System.out.print("Enter algorithm (LCR, HS): ");
        String algorithmType = scanner.next().toUpperCase();
        while (!algorithmType.equals("LCR") && !algorithmType.equals("HS")) {
            System.out.print("Invalid algorithm. Use 'LCR' or 'HS': ");
            algorithmType = scanner.next().toUpperCase();
        }

        // Prompt for ID assignment type
        System.out.print("Enter ID assignment type (ascending, descending, random): ");
        String idType = scanner.next().toLowerCase();
        while (!idType.equals("ascending") && !idType.equals("descending") && !idType.equals("random")) {
            System.out.print("Invalid ID type. Use 'ascending', 'descending', or 'random': ");
            idType = scanner.next().toLowerCase();
        }

        if (runType.equals("single")) {
            // Single run: prompt for ring size
            System.out.print("Enter ring size (n): ");
            int n = scanner.nextInt();
            while (n <= 0) {
                System.out.print("Ring size must be positive. Enter ring size (n): ");
                n = scanner.nextInt();
            }
            runSimulation(n, algorithmType, idType, alpha);
        } else {
            // Multiple runs: predefined ring sizes
            int[] ringSizes = {50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
            for (int n : ringSizes) {
                System.out.println("Starting simulation for n = " + n);
                runSimulation(n, algorithmType, idType, alpha);
            }
        }

        scanner.close();
    }

    
    /**
     * Runs a single leader election simulation.
     *
     * @param n The number of processors in the ring.
     * @param algorithmType The election algorithm to use (LCR or HS).
     * @param idType The type of ID assignment (ascending, descending, random).
     * @param alpha A constant for random ID generation.
     */
    private static void runSimulation(int n, String algorithmType, String idType, int alpha) {
        // Generate IDs based on the chosen type and ring size
        int[] ids = RingNetwork.generateIDs(n, idType, alpha);

        // Create algorithm instance based on user choice
        LeaderElectionAlgorithm algorithm;
        if (algorithmType.equals("LCR")) {
            algorithm = new LCRAlgorithm();
        } else {
            algorithm = new HSAlgorithm();
        }

        // Initialize the ring network with the current size, algorithm, and IDs
        RingNetwork network = new RingNetwork(n, algorithm, ids);
        Processor[] ring = network.getRing();

        int round = 0;
        int messageCount = 0;

        // Run the simulation
        while (true) {
            round++;
            // Check if all processors have terminated
            boolean allTerminated = true;
            for (Processor p : ring) {
                if (!p.isTerminated()) {
                    allTerminated = false;
                    break;
                }
            }
            if (allTerminated) break;  // Election process is complete

            // Collect messages to send in this round
            Map<Processor, Map<String, Message>> messagesToSend = new HashMap<>();
            for (Processor p : ring) {
                Map<String, Message> msgs = p.getMessagesToSend(round);
                if (!msgs.isEmpty()) {
                    messagesToSend.put(p, msgs);
                    messageCount += msgs.size();
                }
            }

            // Distribute messages to neighbors
            for (Processor p : ring) {
                p.setReceivedFromClockwise(null);
                p.setReceivedFromCounterclockwise(null);
                if (messagesToSend.containsKey(p.getClockwiseNeighbour())) {
                    Map<String, Message> neighbourMsgs = messagesToSend.get(p.getClockwiseNeighbour());
                    if (neighbourMsgs.containsKey("counterclockwise")) {
                        p.setReceivedFromClockwise(neighbourMsgs.get("counterclockwise"));
                    }
                }
                if (messagesToSend.containsKey(p.getCounterclockwiseNeighbour())) {
                    Map<String, Message> neighbourMsgs = messagesToSend.get(p.getCounterclockwiseNeighbour());
                    if (neighbourMsgs.containsKey("clockwise")) {
                        p.setReceivedFromCounterclockwise(neighbourMsgs.get("clockwise"));
                    }
                }
            }
        }

        // Verify correctness of the election
        int leaderCount = 0;
        int electedLeaderID = -1;
        for (Processor p : ring) {
            if ("leader".equals(p.getStatus())) {
                leaderCount++;
                electedLeaderID = p.getMyID();
            }
        }
        // Ensure only one leader was elected and all processors recognize the same leader
        boolean correct = leaderCount == 1;
        for (Processor p : ring) {
            if (p.getLeaderID() == null || p.getLeaderID() != electedLeaderID) {
                correct = false;
                break;
            }
        }

        // Output results
        System.out.println("Ring size: " + n + ", Algorithm: " + algorithmType + ", ID type: " + idType);
        System.out.println("Rounds: " + round + ", Messages: " + messageCount + ", Correct: " + correct);
        System.out.println("---------------------");
    }
}