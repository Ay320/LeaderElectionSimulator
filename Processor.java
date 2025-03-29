import java.util.Map;
/**
 * Models a processor (node) in the ring network, maintaining its state and interacting with neighbors via messages.
 * follows the round structure: read messages (step 1), update state (step 2), generate messages (step 3).
 */
public class Processor {
    private int myID;
    private String status = "unknown";         // "unknown" or "leader"
    private Integer leaderID = null;           // ID of the elected leader, null until set
    private Message receivedFromClockwise = null;     // Message from clockwise neighbor
    private Message receivedFromCounterclockwise = null; // Message from counterclockwise neighbor
    private Processor clockwiseNeighbour;
    private Processor counterclockwiseNeighbour;
    private LeaderElectionAlgorithm algorithm;  // algorithm in use

    /**
     * Constructs a processor with a unique ID and an associated leader election algorithm.
     *
     * @param id        The unique ID of the processor.
     * @param algorithm The leader election algorithm to use (LCR or HS).
     */
    public Processor(int id, LeaderElectionAlgorithm algorithm) {
        this.myID = id;
        this.algorithm = algorithm;
        algorithm.initialize(this);
    }


    /**
     * Retrieves the messages that this processor needs to send in the current round.
     * Called in step 3 of a round after reading messages and updating state.
     *
     * @param round The current round of the election process.
     * @return A map of messages to be sent, keyed by direction ("clockwise" or "counterclockwise").
     */
    public Map<String, Message> getMessagesToSend(int round) {
        return algorithm.getMessagesToSend(this, round);
    }

    // Getters and setters:

    /**
     * Gets the processor's unique ID.
     *
     * @return The processor's ID.
     */
    public int getMyID() {
        return myID;
    }

    /**
     * Gets the current status of the processor.
     *
     * @return The processor's status ("unknown" or "leader").
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the processor.
     *
     * @param status The new status ("unknown" or "leader").
     */
    public void setStatus(String status) {
        this.status = status;
    }

      /**
     * Gets the ID of the elected leader.
     *
     * @return The leader's ID, or null if not yet determined.
     */
    public Integer getLeaderID() {
        return leaderID;
    }

     /**
     * Sets the leader ID once the election is complete.
     *
     * @param leaderID The elected leader's ID.
     */
    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    /**
     * Gets the message received from the clockwise neighbor in the last round.
     *
     * @return The message received from the clockwise neighbor.
     */
    public Message getReceivedFromClockwise() {
        return receivedFromClockwise;
    }

      /**
     * Sets the message received from the clockwise neighbor.
     *
     * @param msg The message received.
     */
    public void setReceivedFromClockwise(Message msg) {
        this.receivedFromClockwise = msg;
    }


     /**
     * Gets the message received from the counterclockwise neighbor in the last round.
     *
     * @return The message received from the counterclockwise neighbor.
     */
    public Message getReceivedFromCounterclockwise() {
        return receivedFromCounterclockwise;
    }

    /**
     * Sets the message received from the counterclockwise neighbor.
     *
     * @param msg The message received.
     */
    public void setReceivedFromCounterclockwise(Message msg) {
        this.receivedFromCounterclockwise = msg;
    }

    /**
     * Gets the clockwise neighbor of this processor.
     *
     * @return The processor in the clockwise direction.
     */
    public Processor getClockwiseNeighbour() {
        return clockwiseNeighbour;
    }

    /**
     * Sets the clockwise neighbor of this processor.
     *
     * @param neighbour The processor to set as the clockwise neighbor.
     */
    public void setClockwiseNeighbour(Processor neighbour) {
        this.clockwiseNeighbour = neighbour;
    }

    /**
     * Gets the counterclockwise neighbor of this processor.
     *
     * @return The processor in the counterclockwise direction.
     */
    public Processor getCounterclockwiseNeighbour() {
        return counterclockwiseNeighbour;
    }

     /**
     * Sets the counterclockwise neighbor of this processor.
     *
     * @param neighbour The processor to set as the counterclockwise neighbor.
     */
    public void setCounterclockwiseNeighbour(Processor neighbour) {
        this.counterclockwiseNeighbour = neighbour;
    }


    /**
     * Checks whether the election process has terminated for this processor.
     *
     * @return true if the election is complete, false otherwise.
     */
    public boolean isTerminated() {
        return algorithm.isTerminated(this);
    }
}