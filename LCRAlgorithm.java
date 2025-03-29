import java.util.HashMap;
import java.util.Map;
/**
 * Implementation of the LCR algorithm to elect a leader from multiple proccessors.
 * The processor with the highest ID becomes the leader.
 */
public class LCRAlgorithm implements LeaderElectionAlgorithm {
    /**
     * Initializes the processor for the LCR algorithm.
     * No specific setup is required before the election starts.
     *
     * @param processor The processor participating in the election.
     */
    @Override
    public void initialize(Processor processor) {
        // No specific initialization needed here
    }


    /**
     * Generates messages to be sent in the current round for the LCR algorithm.
     *
     * @param processor The processor participating in the election.
     * @param round The current round.
     * @return A map containing messages, where the key is the direction ("clockwise")
     *         and the value is the message to be sent.
     */
    @Override
    public Map<String, Message> getMessagesToSend(Processor processor, int round) {
        Map<String, Message> messages = new HashMap<>();
        if (round == 1) {
            // Round 1: Send own ID clockwise
            messages.put("clockwise", new Message("election", processor.getMyID()));
        } else {
            // Retrieve the message received from the counterclockwise direction.
            Message received = processor.getReceivedFromCounterclockwise();
            if (received != null) {
                if ("election".equals(received.getType())) {
                    if (received.getId() > processor.getMyID()) {
                        // Forward larger ID clockwise
                        messages.put("clockwise", new Message("election", received.getId()));
                    } else if (received.getId() == processor.getMyID()) {
                        // Received own ID: become leader, send termination
                        processor.setStatus("leader");
                        processor.setLeaderID(processor.getMyID());
                        // Notify others by sending a termination message.
                        messages.put("clockwise", new Message("termination", processor.getMyID()));
                    }
                    // If received ID < myID, do nothing
                    
                } else if ("termination".equals(received.getType())) {
                    // Set leader ID and forward termination unless leader receiving it back
                    processor.setLeaderID(received.getId());
                    if (!"leader".equals(processor.getStatus()) || received.getId() != processor.getMyID()) {
                        messages.put("clockwise", new Message("termination", received.getId()));
                    }
                }
            }
        }
        return messages;
    }

     /**
     * Checks whether the leader election process has terminated for this processor.
     * The process terminates when a leader ID has been set.
     *
     * @param processor The processor whose termination status is checked.
     * @return True if terminated, false otherwise.
     */
    @Override
    public boolean isTerminated(Processor processor) {
        return processor.getLeaderID() != null;
    }
}