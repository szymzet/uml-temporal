package umltemporal.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import umltemporal.core.NodeType;
import umltemporal.core.UmlNode;

public class TestUmlNode {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConstruction() {
        UmlNode n = new UmlNode("id", NodeType.DECISION_NODE);
        assertEquals("id", n.getID().toString());
        assertEquals(NodeType.DECISION_NODE, n.getNodeType());
        assertTrue(n.getIncoming().isEmpty());
        assertTrue(n.getOutgoing().isEmpty());
    }

    @Test
    public void testGetName_noSpaces() {
        UmlNode n = new UmlNode("xd", NodeType.OPAQUE_ACTION);
        n.setName("asdfg");
        assertEquals("asdfg", n.getName());
    }

    @Test
    public void testGetName_quoteWithSpaces() {
        UmlNode n = new UmlNode("xd", NodeType.OPAQUE_ACTION);
        n.setName("as dfg");
        assertEquals("'as dfg'", n.getName());
    }

    @Test
    public void testGetName_notSet() {
        UmlNode n = new UmlNode("xd", NodeType.OPAQUE_ACTION);
        assertEquals(null, n.getName());
    }
}
