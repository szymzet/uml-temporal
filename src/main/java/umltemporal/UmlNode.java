package umltemporal;

import java.util.ArrayList;
import java.util.List;

/**
 * Direct representation of a &lt;node&gt; tag in imported file.
 */
public class UmlNode extends UmlElement {

	private NodeType nodeType;
	private List<String> incoming;
	private List<String> outgoing;
	private String name;

	public List<String> getIncoming() {
		return incoming;
	}

	public void setIncoming(List<String> incoming) {
		this.incoming = incoming;
	}

	public List<String> getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(List<String> outgoing) {
		this.outgoing = outgoing;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public UmlNode(String id, NodeType nodeType) {
		super(id);
		this.nodeType = nodeType;
		this.incoming = new ArrayList<String>();
		this.outgoing = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
