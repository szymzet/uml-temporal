/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umltemporal.test;

import java.io.File;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import umltemporal.ActivityDiagramGraph;
import umltemporal.Expression;
import umltemporal.TemporalGenerator;
import umltemporal.UmlActivityDiagram;
import umltemporal.XmiFileReader;

/**
 *
 * @author szymzet
 */
public class TestTemporalGenerator {

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
    public void testExpression_longSeq() throws Exception {
        longSeqRdr.parse();
        UmlActivityDiagram diagram = longSeqRdr.getActivityDiagrams().get(0);
        ActivityDiagramGraph graph = new ActivityDiagramGraph(diagram);
        TemporalGenerator generator = new TemporalGenerator(graph);
        Expression expression = generator.buildExpression();
        System.out.println(expression.toString());
    }
}
