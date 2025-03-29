/**
 * Represents a message in the leader election process.
 * Messages are used for both LCR and HS algorithms.
 */
public class Message {
    private String type;       // "election" or "termination" for LCR, "hs" for HS
    private int id;           // Processor's ID
    private String direction; // "out" or "in" for HS, null for LCR
    private int hopCount;     // Remaining hops for HS, 0 for LCR

    /**
     * Constructor for LCR algorithm messages.
     * LCR messages only require a type and ID.
     *
     * @param type The type of the message ("election" or "termination").
     * @param id The processor ID associated with the message.
     */
    public Message(String type, int id) {
        this.type = type;
        this.id = id;
        this.direction = null;
        this.hopCount = 0;
    }

    
    /**
     * Constructor for HS algorithm messages.
     * HS messages include a type, processor ID, direction, and hop count.
     *
     * @param type The type of the message ("hs").
     * @param id The processor ID associated with the message.
     * @param direction The direction of the message ("out" or "in").
     * @param hopCount The number of hops the message has remaining.
     */
    public Message(String type, int id, String direction, int hopCount) {
        this.type = type;
        this.id = id;
        this.direction = direction;
        this.hopCount = hopCount;
    }


    /**
     * Gets the type of the message.
     *
     * @return The type of the message ("election", "termination", "hs").
     */
    public String getType() {
        return type;
    }


    /**
     * Gets the processor ID associated with the message.
     *
     * @return The processor ID.
     */
    public int getId() {
        return id;
    }


    /**
     * Gets the direction of the message (applicable to HS algorithm).
     *
     * @return The direction ("out" or "in"), or null for LCR.
     */
    public String getDirection() {
        return direction;
    }


     /**
     * Gets the remaining hop count (applicable to HS algorithm).
     *
     * @return The number of hops left, or 0 for LCR.
     */
    public int getHopCount() {
        return hopCount;
    }
}