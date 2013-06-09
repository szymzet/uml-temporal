package umltemporal.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import umltemporal.UmlActivityDiagram;
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

}
