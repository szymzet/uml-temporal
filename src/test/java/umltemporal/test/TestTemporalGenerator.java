/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umltemporal.test;

import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import umltemporal.core.ActivityDiagramGraph;
import umltemporal.core.TemporalGenerator;
import umltemporal.core.UmlActivityDiagram;
import umltemporal.core.XmiFileReader;

/**
 *
 * @author szymzet
 */
public class TestTemporalGenerator {

    private XmiFileReader airportCheckinRdr;
    private XmiFileReader nestedLoopsRdr;
    private XmiFileReader bigAllPatterns;

    private File getFile(String resourcePath) {
        return new File(this.getClass().getResource(resourcePath).getFile());
    }

    private void compareGeneratorOutput(String expected, String actual) {
        final String strippedExpected = expected.replaceAll("\\s", ""),
            strippedActual = actual.replaceAll("\\s", "");
        assertEquals(strippedExpected, strippedActual);
    }

    private String getExpressionString(XmiFileReader rdr) throws Exception {
        rdr.parse();
        UmlActivityDiagram diagram = rdr.getActivityDiagrams().get(0);
        ActivityDiagramGraph graph = new ActivityDiagramGraph(diagram);
        TemporalGenerator generator = new TemporalGenerator(graph);
        return generator.buildExpression().toString();
    }

    @Before
    public void setUp() {
        airportCheckinRdr = new XmiFileReader(getFile("/AirportCheckin.xmi"));
        nestedLoopsRdr = new XmiFileReader(getFile("/NestedLoops.xmi"));
        bigAllPatterns = new XmiFileReader(getFile("/BigAllPatterns.xmi"));
    }

    @Test
    public void testExpression_bigAllPatterns() throws Exception {
        final String expected =
            "Seq("
            + " Concur("
            + "  Loop("
            + "   'a'"
            + "   'cond1'"
            + "   Branch('b' Seq('c' 'd') Seq('e' 'f'))"
            + "  )"
            + "  'j'"
            + "  Concur("
            + "   'm'"
            + "   'g'"
            + "   Seq('h' 'i')   "
            + "  )"
            + " )"
            + " Branch("
            + "  'p'"
            + "  'r'"
            + "  Concur('n' 'k' Loop('o' 'cond4' 'l'))"
            + " )"
            + ")";

        compareGeneratorOutput(expected, getExpressionString(bigAllPatterns));
    }

    @Test
    public void testExpression_airportCheckin() throws Exception {
        final String expected =
            "Seq(\n"
            + "  'Show ticket'\n"
            + "  Branch(\n"
            + "    'Verify ticket'\n"
            + "    Seq(\n"
            + "      'Check luggage'\n"
            + "      Seq(\n"
            + "        Branch(\n"
            + "          'Accept luggage'\n"
            + "          'Pay fee'\n"
            + "          'Do not pay'\n"
            + "        )\n"
            + "        'Issue boarding pass'\n"
            + "      )\n"
            + "    )\n"
            + "    'Refer to customer service'\n"
            + "  )\n"
            + ")\n";

        compareGeneratorOutput(expected, getExpressionString(airportCheckinRdr));
    }

    @Test
    public void testExpression_nestedLoops() throws Exception {
        final String expected = "Loop('Action' 'condition1'"
            + "Loop('Action1' 'condition2' "
            + "Loop('Action2' 'condition3' 'Action3')))";

        compareGeneratorOutput(expected, getExpressionString(nestedLoopsRdr));
    }
}
