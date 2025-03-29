import java.util.Map;
/**
 * Interface representing a leader election algorithm in a distributed system.
 */
public interface LeaderElectionAlgorithm {

    /**
     * This method sets up any necessary state for the processor to participate in the election.
     * @param processor The processor that is part of the election process.
     */
    void initialize(Processor processor);


     /**
     * Determines the messages that the given processor should send during a specific round.
     * @param processor The processor participating in the election.
     * @param round The current round of the election process.
     * @return A map of messages, where the key is the direction ("clockwise" for LCR, both directions for HS)
     *         and the value is the message to be sent.
     */
    Map<String, Message> getMessagesToSend(Processor processor, int round);


      /**
     * Checks whether the election process has terminated for the given processor.
     * @param processor The processor whose termination status is being checked.
     * @return True if terminated (leader ID is set), false otherwise.
     */
    boolean isTerminated(Processor processor);
}