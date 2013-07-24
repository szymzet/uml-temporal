/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umltemporal.test;

import org.junit.Test;
import umltemporal.core.ActionExpression;
import umltemporal.core.Expression;
import umltemporal.core.SequenceExpression;
import static org.junit.Assert.assertEquals;
import umltemporal.core.BranchingExpression;
import umltemporal.core.ConcurrencyExpression;
import umltemporal.core.LoopExpression;

/**
 *
 * @author szymzet
 */
public class TestExpression {

    @Test
    public void testGetStringExpression_Action() {
        // we use it only for the arguments
        Expression action = new ActionExpression("asdf2ASDF");
        assertEquals("", action.getStringExpression());
    }

    @Test
    public void testGetStringExpression_Sequence() {
        Expression seq = new SequenceExpression();
        seq.addArg(new ActionExpression("a"));
        seq.addArg(new ActionExpression("b"));
        final String expected =
            "a => <>b\n"
            + "[]~(a & b)\n";
        assertEquals(expected, seq.getStringExpression());
    }

    @Test
    public void testGetStringExpression_Branching() {
        Expression branch = new BranchingExpression();
        branch.addArg(new ActionExpression("a"));
        branch.addArg(new ActionExpression("b"));
        branch.addArg(new ActionExpression("c"));
        final String expected =
            "a => (<>b & ~<>c) | (~<>b & <>c)\n"
            + "[]~(b & c)\n";
        assertEquals(expected, branch.getStringExpression());
    }

    @Test
    public void testGetStringExpression_Concurrency() {
        Expression branch = new ConcurrencyExpression();
        branch.addArg(new ActionExpression("a"));
        branch.addArg(new ActionExpression("b"));
        branch.addArg(new ActionExpression("c"));
        final String expected =
            "a => <>b & <>c\n"
            + "[]~(a & (b | c))\n";
        assertEquals(expected, branch.getStringExpression());
    }

    @Test
    public void testGetStringExpression_Loop() {
        Expression branch = new LoopExpression();
        branch.addArg(new ActionExpression("a"));
        branch.addArg(new ActionExpression("b"));
        branch.addArg(new ActionExpression("c"));
        final String expected =
            "a => <>b\n"
            + "b & cond(b) => <>c\n"
            + "b & ~cond(b) => ~<>c\n"
            + "c => <>b\n"
            + "[]~((a & b) | (b & c) | (a & c))\n";
        assertEquals(expected, branch.getStringExpression());
    }
}
