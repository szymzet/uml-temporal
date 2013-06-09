package umltemporal;

import java.util.List;

public final class UmlActivityDiagram {

	private List<UmlEdge> edges;
	private List<UmlNode> nodes;

	public UmlActivityDiagram(List<UmlEdge> edges, List<UmlNode> nodes) {
		this.edges = edges;
		this.nodes = nodes;
	}

	public List<UmlEdge> getEdges() {
		return edges;
	}

	public List<UmlNode> getNodes() {
		return nodes;
	}

}
