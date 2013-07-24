package umltemporal.core;

/**
 * Direct representation of &lt;edge&gt; tag in imported file
 */
public class UmlEdge extends UmlElement {

    private String sourceID;
    private String targetID;
    private String guard;

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    /**
     * Get ID of the element being the source of the edge.
     *
     * @return
     */
    public String getSourceID() {
        return sourceID;
    }

    /**
     * Get ID of the element at which the edge 'points'.
     *
     * @return
     */
    public String getTargetID() {
        return targetID;
    }

    public UmlEdge(String id, String sourceID, String targetID) {
        super(id);
        this.sourceID = sourceID;
        this.targetID = targetID;
    }
}
