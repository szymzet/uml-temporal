package umltemporal.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author szymzet
 */
public class ActionExpression extends Expression {

    public ActionExpression(String name) {
        super(name);
    }

    @Override
    protected List<String> getExpressionLinesAndArgs(List<String> args) {
        String n = name.matches(".*\\s.*") ? "'" + name + "'" : name;
        args.add(n);
        return new ArrayList<String>();
    }

    @Override
    protected List<String> getPatternExpressions(List<String> currentArgs) {
        return new ArrayList<String>();
    }
}
