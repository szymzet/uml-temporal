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
public class LoopExpression extends Expression {

    public LoopExpression() {
        super("Loop");
    }

    @Override
    protected List<String> getPatternExpressions(List<String> currentArgs) {
        List<String> expressions = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();
        sb.append(currentArgs.get(0));
        sb.append(" => <>");
        sb.append(currentArgs.get(1));
        expressions.add(sb.toString());

        sb = new StringBuilder();
        sb.append(currentArgs.get(1));
        sb.append(" & cond(");
        sb.append(currentArgs.get(1));
        sb.append(") => <>");
        sb.append(currentArgs.get(2));
        expressions.add(sb.toString());

        sb = new StringBuilder();
        sb.append(currentArgs.get(1));
        sb.append(" & ~cond(");
        sb.append(currentArgs.get(1));
        sb.append(") => ~<>");
        sb.append(currentArgs.get(2));
        expressions.add(sb.toString());

        sb = new StringBuilder();
        sb.append(currentArgs.get(2));
        sb.append(" => <>");
        sb.append(currentArgs.get(1));
        expressions.add(sb.toString());

        sb = new StringBuilder("[]~((");
        sb.append(currentArgs.get(0));
        sb.append(" & ");
        sb.append(currentArgs.get(1));
        sb.append(") | (");
        sb.append(currentArgs.get(1));
        sb.append(" & ");
        sb.append(currentArgs.get(2));
        sb.append(") | (");
        sb.append(currentArgs.get(0));
        sb.append(" & ");
        sb.append(currentArgs.get(2));
        sb.append("))");
        expressions.add(sb.toString());

        return expressions;
    }
}
