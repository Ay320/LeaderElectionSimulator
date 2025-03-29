import java.util.*;
/**
 * Represents a ring network of processors participating in a leader election algorithm.
 * The processors are connected in a circular topology.
 */
public class RingNetwork {
    private Processor[] ring;
    private int size;

    /**
     * Constructs a ring network of processors for leader election.
     *
     * @param n         The number of processors in the ring.
     * @param algorithm The leader election algorithm to be used.
     * @param ids       The array of unique IDs assigned to each processor.
     * @throws IllegalArgumentException If the length of the ID array does not match the ring size.
     */
    public RingNetwork(int n, LeaderElectionAlgorithm algorithm, int[] ids) {
        this.size = n;
        if (ids.length != n) {
            throw new IllegalArgumentException("ID array length must match ring size");
        }
        ring = new Processor[n];
        for (int i = 0; i < n; i++) {
            ring[i] = new Processor(ids[i], algorithm);
        }
        for (int i = 0; i < n; i++) {
            ring[i].setClockwiseNeighbour(ring[(i + 1) % n]);
            ring[i].setCounterclockwiseNeighbour(ring[(i - 1 + n) % n]);
        }
    }

    
    /**
     * Generates an array of unique processor IDs based on the specified type.
     *
     * @param n     The number of processors in the ring.
     * @param type  The type of ID assignment ("ascending", "descending", or "random").
     * @param alpha A scaling factor for generating random IDs.
     * @return An array of unique processor IDs.
     * @throws IllegalArgumentException If an invalid ID type is provided.
     */
    public static int[] generateIDs(int n, String type, int alpha) {
        int[] ids = new int[n];
        if ("ascending".equalsIgnoreCase(type)) {
            for (int i = 0; i < n; i++) ids[i] = i + 1;
        } else if ("descending".equalsIgnoreCase(type)) {
            for (int i = 0; i < n; i++) ids[i] = n - i;
        } else if ("random".equalsIgnoreCase(type)) {
            Random rand = new Random();
            Set<Integer> used = new HashSet<>();
            int maxID = alpha * n;
            for (int i = 0; i < n; i++) {
                int id;
                do {
                    id = rand.nextInt(maxID) + 1;
                } while (used.contains(id));
                used.add(id);
                ids[i] = id;
            }
        } else {
            throw new IllegalArgumentException("Invalid ID type: " + type);
        }
        return ids;
    }

      /**
     * Returns the array of processors forming the ring network.
     *
     * @return The processor array.
     */
    public Processor[] getRing() {
        return ring;
    }

     /**
     * Returns the size of the ring network.
     *
     * @return The number of processors in the ring.
     */
    public int getSize() {
        return size;
    }
}