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
        Expression concur = new ConcurrencyExpression();
        concur.addArg(new ActionExpression("a"));
        concur.addArg(new ActionExpression("b"));
        concur.addArg(new ActionExpression("c"));
        final String expected =
            "a => <>b & <>c\n"
            + "[]~(a & (b | c))\n";
        assertEquals(expected, concur.getStringExpression());
    }

    @Test
    public void testGetStringExpression_Loop() {
        Expression loop = new LoopExpression();
        loop.addArg(new ActionExpression("a"));
        loop.addArg(new ActionExpression("b"));
        loop.addArg(new ActionExpression("c"));
        final String expected =
            "a => <>b\n"
            + "b & cond(b) => <>c\n"
            + "b & ~cond(b) => ~<>c\n"
            + "c => <>b\n"
            + "[]~((a & b) | (b & c) | (a & c))\n";
        assertEquals(expected, loop.getStringExpression());
    }

    @Test
    public void testGetStringExpression_Complex1() {
        Expression branch = new BranchingExpression();
        branch.addArg(new ActionExpression("x"));
        branch.addArg(new ActionExpression("y"));
        branch.addArg(new ActionExpression("z"));

        Expression seq = new SequenceExpression();
        seq.addArg(new ActionExpression("p"));
        seq.addArg(new ActionExpression("q"));

        Expression concur = new ConcurrencyExpression();
        concur.addArg(seq);
        concur.addArg(new ActionExpression("m"));
        concur.addArg(new ActionExpression("n"));

        Expression loop = new LoopExpression();
        loop.addArg(branch);
        loop.addArg(new ActionExpression("b"));
        loop.addArg(concur);
        final String expected =
            "x => (<>y & ~<>z) | (~<>y & <>z)\n" // Branching(x y z)
            + "[]~(y & z)\n"
            + "p => <>q\n" // Seq(p q)
            + "[]~(p & q)\n"
            + "(p | q) => <>m & <>n\n" // Concur((p|q) m n)
            + "[]~((p | q) & (m | n))\n"
            + "(x | y | z) => <>b\n" // Loop((x|y|z) b (p|q|m|n))
            + "b & cond(b) => <>(p | q | m | n)\n"
            + "b & ~cond(b) => ~<>(p | q | m | n)\n"
            + "(p | q | m | n) => <>b\n"
            + "[]~(((x | y | z) & b) | (b & (p | q | m | n)) | ((x | y | z) & (p | q | m | n)))\n";

        assertEquals(expected, loop.getStringExpression());
        System.out.println(loop.getStringExpression());
    }
}
