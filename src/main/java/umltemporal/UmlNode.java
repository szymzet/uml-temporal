package umltemporal;

/**
 * Direct representation of a &lt;node&gt; tag in imported file.
 */
public class UmlNode extends UmlElement {

	private NodeType nodeType;

	public NodeType getNodeType() {
		return nodeType;
	}

	public UmlNode(String id, NodeType nodeType) {
		super(id);
		this.nodeType = nodeType;
	}

	public String[] getIncoming() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getOutgoing() {
		// TODO Auto-generated method stub
		return null;
	}
}
