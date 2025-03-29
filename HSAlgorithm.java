import java.util.*;

/**
 * Implementation of the HS algorithm to elect a leader from multiple proccessors.
 * This algorithm operates in a bidirectional ring topology where 
 * each processor has a unique ID, and the highest ID is elected as the leader.
 */
public class HSAlgorithm implements LeaderElectionAlgorithm {
    private Map<Processor, Integer> phaseMap = new HashMap<>();              // Current phase per processor
    private Map<Processor, Boolean> activeMap = new HashMap<>();             // Whether processor is still initiating messages
    private Map<Processor, Boolean> receivedClockwiseIn = new HashMap<>();   // Tracks "in" message from clockwise
    private Map<Processor, Boolean> receivedCounterclockwiseIn = new HashMap<>(); // Tracks "in" from counterclockwise


    /**
     * Initializes the processor for the HS algorithm by setting its phase to 0, marking it active,
     * and resetting flags for receiving "in" messages.
     * Prepares the processor for phased execution.
     *
     * @param processor The processor to initialize.
     */
    @Override
    public void initialize(Processor processor) {
        phaseMap.put(processor, 0);
        activeMap.put(processor, true);
        receivedClockwiseIn.put(processor, false);
        receivedCounterclockwiseIn.put(processor, false);
    }


     /**
     * Generate the messages a processor should send in the given election round.
     *
     * @param processor The processor participating in the election.
     * @param round The current round number.
     * @return A map containing messages to send, where the key is the direction ("clockwise" or "counterclockwise")
     *         and the value is the message.
     */
    @Override
    public Map<String, Message> getMessagesToSend(Processor processor, int round) {
        Map<String, Message> messages = new HashMap<>();

        // If terminated, only forward termination messages
        if (processor.getLeaderID() != null) {
            forwardMessages(processor, messages);
            return messages;
        }

        // If active and either first round or both "in" messages received, start new phase
        if (activeMap.get(processor) && (round == 1 || (receivedClockwiseIn.get(processor) && receivedCounterclockwiseIn.get(processor)))) {
            int phase = phaseMap.get(processor);
            int hopCount = (int) Math.pow(2, phase);
            // Send "out" messages in both directions
            messages.put("clockwise", new Message("hs", processor.getMyID(), "out", hopCount));
            messages.put("counterclockwise", new Message("hs", processor.getMyID(), "out", hopCount));
            // Reset tracking of received "in" messages
            receivedClockwiseIn.put(processor, false);
            receivedCounterclockwiseIn.put(processor, false);
             // Move to next phase
            phaseMap.put(processor, phase + 1);
        }

        // Process received messages 
        forwardMessages(processor, messages);
        return messages;
    }


    /**
     * Handles received messages and determines what should be forwarded.
     *
     * @param processor The processor processing messages.
     * @param messages The map of messages to be sent.
     */
    private void forwardMessages(Processor processor, Map<String, Message> messages) {
        Message receivedCW = processor.getReceivedFromClockwise();
        Message receivedCCW = processor.getReceivedFromCounterclockwise();

        // Process messages received from clockwise direction
        if (receivedCW != null) {
            if ("hs".equals(receivedCW.getType())) {
                processHSMessage(processor, receivedCW, "clockwise", messages);
            } else if ("termination".equals(receivedCW.getType())) {
                processor.setLeaderID(receivedCW.getId());
                messages.put("clockwise", receivedCW);
                messages.put("counterclockwise", receivedCW);
            }
        }
        // Process messages received from counterclockwise direction
        if (receivedCCW != null) {
            if ("hs".equals(receivedCCW.getType())) {
                processHSMessage(processor, receivedCCW, "counterclockwise", messages);
            } else if ("termination".equals(receivedCCW.getType())) {
                processor.setLeaderID(receivedCCW.getId());
                messages.put("clockwise", receivedCCW);
                messages.put("counterclockwise", receivedCCW);
            }
        }
    }


    /**
     * Processes an "hs" message received from a neighbor. Depending on the hop count and ID:
     * - Forwards the message if hops > 1.
     * - Converts to an "in" message if hops = 1.
     * - Updates state (e.g., becomes inactive) if the ID is larger than the processor’s own.
     *
     * @param processor     The processor processing the message.
     * @param msg           The received "hs" message.
     * @param fromDirection The direction from which the message was received.
     * @param messages      The map to populate with messages to send.
     */
    private void processHSMessage(Processor processor, Message msg, String fromDirection, Map<String, Message> messages) {
        // Determine the opposite direction
        String oppositeDirection;
        if ("clockwise".equals(fromDirection)){
            oppositeDirection = "counterclockwise";
        } else{
            oppositeDirection = "clockwise";
        }

        if ("out".equals(msg.getDirection())) {
            if (msg.getId() > processor.getMyID() && msg.getHopCount() > 1) {
                // Forward "out" message with decremented hop count
                messages.put(oppositeDirection, new Message("hs", msg.getId(), "out", msg.getHopCount() - 1));
            } else if (msg.getId() > processor.getMyID() && msg.getHopCount() == 1) {
                // Turn "out" into "in" message
                messages.put(fromDirection, new Message("hs", msg.getId(), "in", 1));
                activeMap.put(processor, false); // Stop sending if overtaken
            } else if (msg.getId() == processor.getMyID()) {
                // If the processor receives its own "out" message, it becomes the leader
                processor.setStatus("leader");
                processor.setLeaderID(processor.getMyID());

                // Notify all processors by sending a termination message in both directions
                Message terminationMsg = new Message("termination", processor.getMyID());
                messages.put("clockwise", terminationMsg);
                messages.put("counterclockwise", terminationMsg);
                activeMap.put(processor, false);
            }
        } else if ("in".equals(msg.getDirection())) {
            if (msg.getId() != processor.getMyID()) {
                // Forward "in" message back toward origin
                messages.put(oppositeDirection, new Message("hs", msg.getId(), "in", 1));
            } else {
                // If the processor receives its own "in" message, mark it received
                if ("clockwise".equals(fromDirection)) {
                    receivedClockwiseIn.put(processor, true);
                } else {
                    receivedCounterclockwiseIn.put(processor, true);
                }
            }
        }
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