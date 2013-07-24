/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umltemporal.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author szymzet
 */
public class SequenceExpression extends Expression {

    public SequenceExpression() {
        super("Seq");
    }

    @Override
    protected List<String> getPatternExpressions(List<String> currentArgs) {
        List<String> expressions = new ArrayList<String>();
        expressions.add(currentArgs.get(0) + " => <>" + currentArgs.get(1));
        expressions.add("[]~(" + currentArgs.get(0) + " & " + currentArgs.get(1) + ")");
        return expressions;
    }
}
