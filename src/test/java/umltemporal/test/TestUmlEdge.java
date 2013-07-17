package umltemporal.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import umltemporal.core.UmlEdge;

public class TestUmlEdge {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConstruction() {
		UmlEdge e = new UmlEdge("id", "sourceID", "targetID");
		assertEquals("sourceID", e.getSourceID());
		assertEquals("targetID", e.getTargetID());
		assertEquals("id", e.getID());
	}
}
