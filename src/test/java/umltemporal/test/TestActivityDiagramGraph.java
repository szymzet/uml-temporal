package umltemporal.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import umltemporal.ActivityDiagramGraph;
import umltemporal.NodeType;
import umltemporal.UmlActivityDiagram;
import umltemporal.UmlEdge;
import umltemporal.UmlNode;

public class TestActivityDiagramGraph {

	private List<UmlNode> nodes;
	private Map<String, UmlNode> nodesMap;
	private List<UmlEdge> edges;
	private UmlActivityDiagram umlDiagram;
	private ActivityDiagramGraph graph;

	@Before
	public void setUp() throws Exception {
		nodes = Arrays
				.asList(new UmlNode("n-final",
									NodeType.ACTIVITY_FINAL_NODE,
									Arrays.asList("e-16"),
									new ArrayList<String>()),
						new UmlNode("n-initial", NodeType.INITIAL_NODE, new ArrayList<String>(), Arrays.asList("e-1")),
						new UmlNode("n-1", NodeType.OPAQUE_ACTION, Arrays.asList("e-1"), Arrays.asList("e-2")),
						new UmlNode("n-2", NodeType.OPAQUE_ACTION, Arrays.asList("e-2"), Arrays.asList("e-3")),
						new UmlNode("n-3", NodeType.FORK_NODE, Arrays.asList("e-3"), Arrays.asList("e-4", "e-5")),
						new UmlNode("n-4", NodeType.OPAQUE_ACTION, Arrays.asList("e-5"), Arrays.asList("e-6")),
						new UmlNode("n-5", NodeType.OPAQUE_ACTION, Arrays.asList("e-6"), Arrays.asList("e-7")),
						new UmlNode("n-6", NodeType.DECISION_NODE, Arrays.asList("e-7", "e-8"), Arrays.asList(	"e-10",
																												"e-11")),
						new UmlNode("n-7", NodeType.OPAQUE_ACTION, Arrays.asList("e-9"), Arrays.asList("e-8")),
						new UmlNode("n-8", NodeType.OPAQUE_ACTION, Arrays.asList("e-10"), Arrays.asList("e-9")),
						new UmlNode("n-9", NodeType.OPAQUE_ACTION, Arrays.asList("e-11"), Arrays.asList("e-12")),
						new UmlNode("n-10", NodeType.OPAQUE_ACTION, Arrays.asList("e-4"), Arrays.asList("e-13")),
						new UmlNode("n-11", NodeType.OPAQUE_ACTION, Arrays.asList("e-13"), Arrays.asList("e-14")),
						new UmlNode("n-12", NodeType.MERGE_NODE, Arrays.asList("e-14", "e-12"), Arrays.asList("e-15")),
						new UmlNode("n-13", NodeType.OPAQUE_ACTION, Arrays.asList("e-15"), Arrays.asList("e-16")));

		nodesMap = new HashMap<String, UmlNode>();
		for (UmlNode n : nodes) {
			nodesMap.put(n.getID(), n);
		}

		edges = Arrays.asList(	new UmlEdge("e-1", "n-initial", "n-1"),
								new UmlEdge("e-2", "n-1", "n-2"),
								new UmlEdge("e-3", "n-2", "n-3"),
								new UmlEdge("e-4", "n-3", "n-10"),
								new UmlEdge("e-5", "n-3", "n-4"),
								new UmlEdge("e-6", "n-4", "n-5"),
								new UmlEdge("e-7", "n-5", "n-6"),
								new UmlEdge("e-8", "n-7", "n-6"),
								new UmlEdge("e-9", "n-8", "n-7"),
								new UmlEdge("e-10", "n-6", "n-8"),
								new UmlEdge("e-11", "n-6", "n-9"),
								new UmlEdge("e-12", "n-9", "n-12"),
								new UmlEdge("e-13", "n-10", "n-11"),
								new UmlEdge("e-14", "n-11", "n-12"),
								new UmlEdge("e-15", "n-12", "n-13"),
								new UmlEdge("e-16", "n-13", "n-final"));

		umlDiagram = new UmlActivityDiagram(edges, nodes);
		graph = new ActivityDiagramGraph(umlDiagram);
	}

	@Test
	public void testGetInitialNode() {
		assertEquals(nodesMap.get("n-initial"), graph.getInitialNode());
	}

	@Test
	public void testGetNext() {
		UmlNode node = graph.getInitialNode();

		List<UmlNode> out1 = graph.getOutgoing(node);
		assertEquals(1, out1.size());
		assertEquals(nodesMap.get("n-1"), out1.get(0));

		List<UmlNode> out2 = graph.getOutgoing(out1.get(0));
		assertEquals(1, out2.size());
		assertEquals(nodesMap.get("n-2"), out2.get(0));

		List<UmlNode> out3 = graph.getOutgoing(out2.get(0));
		assertEquals(1, out3.size());
		assertEquals(nodesMap.get("n-3"), out3.get(0));

		List<UmlNode> out4 = graph.getOutgoing(out3.get(0));
		assertEquals(2, out4.size());
		assertEquals(nodesMap.get("n-10"), out4.get(0));
		assertEquals(nodesMap.get("n-4"), out4.get(1));

		List<UmlNode> out5 = graph.getOutgoing(nodesMap.get("n-6"));
		assertEquals(2, out5.size());
	}

	@Test
	public void testGetEdge() {
		UmlEdge edge = graph.getEdge(nodesMap.get("n-6"), nodesMap.get("n-8"));
		assertEquals("e-10", edge.getID());
	}
}
