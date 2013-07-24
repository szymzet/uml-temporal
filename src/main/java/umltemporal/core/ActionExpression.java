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
        args.add(name);
        return new ArrayList<String>();
    }

    @Override
    protected List<String> getPatternExpressions(List<String> currentArgs) {
        return new ArrayList<String>();
    }
}
