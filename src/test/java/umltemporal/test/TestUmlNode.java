package umltemporal.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import umltemporal.NodeType;
import umltemporal.UmlNode;

public class TestUmlNode {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConstruction() {
		UmlNode n = new UmlNode("id", NodeType.DECISION_NODE);
		assertEquals("id", n.getID().toString());
		assertEquals(NodeType.DECISION_NODE, n.getNodeType());
		assertNull(n.getIncoming());
		assertNull(n.getOutgoing());
	}
}
