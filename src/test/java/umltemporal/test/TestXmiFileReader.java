package umltemporal.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import umltemporal.ActivityDiagramGraph;
import umltemporal.Expression;
import umltemporal.NodeType;
import umltemporal.TemporalGenerator;
import umltemporal.UmlActivityDiagram;
import umltemporal.UmlEdge;
import umltemporal.UmlNode;
import umltemporal.XmiFileReader;

public class TestXmiFileReader {
	XmiFileReader patternsRdr;
	XmiFileReader longSeqRdr;

	private File getFile(String resourcePath) {
		return new File(this.getClass().getResource(resourcePath).getFile());
	}

	@Before
	public void setUp() {
		patternsRdr = new XmiFileReader(getFile("/Patterns.xmi"));
		longSeqRdr = new XmiFileReader(getFile("/LongSequence.xmi"));
	}

	@Test
	public void testParse_correctCountOfDiagrams() throws Exception {
		longSeqRdr.parse();
		List<UmlActivityDiagram> diags1 = longSeqRdr.getActivityDiagrams();
		assertEquals(1, diags1.size());

		patternsRdr.parse();
		List<UmlActivityDiagram> diags2 = patternsRdr.getActivityDiagrams();
		assertEquals(4, diags2.size());
	}

	@Test
	public void testParse_correctCountOfEdges() throws Exception {
		final int[] SIZES = new int[] { 3, 7, 7, 6 };
		patternsRdr.parse();
		List<UmlActivityDiagram> diags = patternsRdr.getActivityDiagrams();
		for (int i = 0; i < diags.size(); ++i) {
			assertEquals(SIZES[i], diags.get(i).getEdges().size());
		}
	}

	@Test
	public void testParse_correctCountOfNodes() throws Exception {
		final int[] SIZES = new int[] { 4, 7, 7, 6 };
		patternsRdr.parse();
		List<UmlActivityDiagram> diags = patternsRdr.getActivityDiagrams();
		for (int i = 0; i < diags.size(); ++i) {
			assertEquals(SIZES[i], diags.get(i).getNodes().size());
		}
	}

	@Test
	public void testParse_correctIdsInEdges() throws Exception {
		final String[][] IDS = { { "_YpNGOtEaEeKmOatZJT2A1A", "_YpNGNtEaEeKmOatZJT2A1A", "_YpNGN9EaEeKmOatZJT2A1A" },
								{ "_YpNtQNEaEeKmOatZJT2A1A", "_YpNGN9EaEeKmOatZJT2A1A", "_YpNGOdEaEeKmOatZJT2A1A" },
								{ "_YpNtQtEaEeKmOatZJT2A1A", "_YpNGONEaEeKmOatZJT2A1A", "_YpNGNtEaEeKmOatZJT2A1A" } };

		patternsRdr.parse();
		List<UmlActivityDiagram> diags = patternsRdr.getActivityDiagrams();
		List<UmlEdge> edges = diags.get(0).getEdges();
		for (int i = 0; i < edges.size(); ++i) {
			assertEquals(IDS[i][0], edges.get(i).getID());
			assertEquals(IDS[i][1], edges.get(i).getSourceID());
			assertEquals(IDS[i][2], edges.get(i).getTargetID());
		}
	}

	@Test
	public void testParse_correctIdsInNodes() throws Exception {
		final UmlNode[] NODES = {	new UmlNode("_YpOUXtEaEeKmOatZJT2A1A", NodeType.INITIAL_NODE),
									new UmlNode("_YpOUX9EaEeKmOatZJT2A1A", NodeType.ACTIVITY_FINAL_NODE),
									new UmlNode("_YpVCANEaEeKmOatZJT2A1A", NodeType.OPAQUE_ACTION),
									new UmlNode("_YpVCAdEaEeKmOatZJT2A1A", NodeType.MERGE_NODE),
									new UmlNode("_YpVCAtEaEeKmOatZJT2A1A", NodeType.DECISION_NODE),
									new UmlNode("_YpVCA9EaEeKmOatZJT2A1A", NodeType.OPAQUE_ACTION) };

		patternsRdr.parse();
		List<UmlActivityDiagram> diags = patternsRdr.getActivityDiagrams();
		List<UmlNode> nodes = diags.get(3).getNodes();
		for (int i = 0; i < nodes.size(); ++i) {
			assertEquals(NODES[i].getID(), nodes.get(i).getID());
			assertEquals(NODES[i].getNodeType(), nodes.get(i).getNodeType());
		}
	}

	@Test
	public void testParse_correctNodeNames() throws Exception {
		final String[] names = { "Initial Node",
								"Decision-Merge",
								"a",
								"b",
								"c",
								"Decision-Merge1",
								"Activity Final Node" };

		patternsRdr.parse();
		List<UmlActivityDiagram> diags = patternsRdr.getActivityDiagrams();
		List<UmlNode> nodes = diags.get(2).getNodes();

		for (int i = 0; i < nodes.size(); ++i) {
			assertEquals(names[i], nodes.get(i).getName());
		}
	}

	@Test
	public void testParse_correctNodeConnections() throws Exception {
		final String[] OUT_INITIAL = { "_YpVCBNEaEeKmOatZJT2A1A" };
		final String[] IN_FINAL = { "_YpVCDNEaEeKmOatZJT2A1A" };
		final String[] OUT_MERGE = { "_YpVCCNEaEeKmOatZJT2A1A" };
		final String[] IN_MERGE = { "_YpVCBtEaEeKmOatZJT2A1A", "_YpVCD9EaEeKmOatZJT2A1A" };
		final String[] OUT_DECISION = { "_YpVCCdEaEeKmOatZJT2A1A", "_YpVCDNEaEeKmOatZJT2A1A" };
		final String[] IN_DECISION = { "_YpVCCNEaEeKmOatZJT2A1A" };
		final String[] OUT_OPAQUE = { "_YpVCD9EaEeKmOatZJT2A1A" };
		final String[] IN_OPAQUE = { "_YpVCCdEaEeKmOatZJT2A1A" };

		patternsRdr.parse();
		List<UmlActivityDiagram> diags = patternsRdr.getActivityDiagrams();
		List<UmlNode> nodes = diags.get(3).getNodes();

		UmlNode n = nodes.get(0);
		assertEquals(NodeType.INITIAL_NODE, n.getNodeType());
		assertArrayEquals(OUT_INITIAL, n.getOutgoing().toArray());
		assertTrue(n.getIncoming().isEmpty());

		n = nodes.get(1);
		assertEquals(NodeType.ACTIVITY_FINAL_NODE, n.getNodeType());
		assertTrue(n.getOutgoing().isEmpty());
		assertArrayEquals(IN_FINAL, n.getIncoming().toArray());

		n = nodes.get(3);
		assertEquals(NodeType.MERGE_NODE, n.getNodeType());
		assertArrayEquals(OUT_MERGE, n.getOutgoing().toArray());
		assertArrayEquals(IN_MERGE, n.getIncoming().toArray());

		n = nodes.get(4);
		assertEquals(NodeType.DECISION_NODE, n.getNodeType());
		assertArrayEquals(OUT_DECISION, n.getOutgoing().toArray());
		assertArrayEquals(IN_DECISION, n.getIncoming().toArray());

		n = nodes.get(5);
		assertEquals(NodeType.OPAQUE_ACTION, n.getNodeType());
		assertArrayEquals(OUT_OPAQUE, n.getOutgoing().toArray());
		assertArrayEquals(IN_OPAQUE, n.getIncoming().toArray());
	}

	@Test
	public void testParse_parsingEdgeGuards() throws Exception {
		patternsRdr.parse();
		UmlActivityDiagram diag = patternsRdr.getActivityDiagrams().get(2);
		List<UmlEdge> edges = diag.getEdges();
		assertEquals(null, edges.get(0).getGuard());
		assertEquals("warunek", edges.get(1).getGuard());
		assertEquals("NOT warunek", edges.get(2).getGuard());

	}

	@Test
	public void testExpression() throws Exception {
		longSeqRdr.parse();
		List<UmlActivityDiagram> diagrams = longSeqRdr.getActivityDiagrams();
		for (UmlActivityDiagram diagram : diagrams) {
			ActivityDiagramGraph graph = new ActivityDiagramGraph(diagram);
			TemporalGenerator generator = new TemporalGenerator(graph);
			Expression expression = generator.buildExpression();
			System.out.println(expression.toString());
		}
	}
}
