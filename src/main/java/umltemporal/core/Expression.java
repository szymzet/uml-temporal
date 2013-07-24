package umltemporal.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Expression {

    protected String name;
    protected List<Expression> args;

    public Expression(String name) {
        this.name = name;
        this.args = new ArrayList<Expression>();
    }

    public void addArg(Expression arg) {
        this.args.add(arg);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String getStringExpression() {
        List<String> argsFromEnclosed = new ArrayList<String>();
        List<String> lines = getExpressionLinesAndArgs(argsFromEnclosed);

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        depthAppend(sb, depth, name);

        if (!args.isEmpty()) {
            sb.append("(");
            sb.append("\n");

            for (Expression arg : args) {
                sb.append(arg.toString(depth + 1));
                sb.append("\n");
            }

            depthAppend(sb, depth, ")");
        }

        return sb.toString();
    }

    protected List<String> getExpressionLinesAndArgs(List<String> outArgs) {
        List<String> subArgs = new ArrayList<String>();
        List<String> currentArgs = new ArrayList<String>();
        List<String> expressions = new ArrayList<String>();

        for (Expression arg : args) {
            List<String> exp = arg.getExpressionLinesAndArgs(subArgs);
            expressions.addAll(exp);
            currentArgs.add(Expression.getArgsDisjunction(subArgs));
            outArgs.addAll(subArgs);
            subArgs.clear();
        }

        List<String> expression = getPatternExpressions(currentArgs);
        expressions.addAll(expression);
        return expressions;

    }

    protected abstract List<String> getPatternExpressions(List<String> currentArgs);

    protected static String getArgsDisjunction(List<String> args) {
        assert args.size() > 0;
        if (args.size() == 1) {
            return args.get(0);
        }

        StringBuilder sb = new StringBuilder("(");
        sb.append(args.get(0));
        for (int i = 1; i < args.size(); ++i) {
            sb.append(" | ");
            sb.append(args.get(i));
        }

        sb.append(")");
        return sb.toString();
    }

    private String getDepth(int depth) {
        final int INDENT_SIZE = 2;
        StringBuilder sb = new StringBuilder(INDENT_SIZE * depth);
        for (int i = 0, len = INDENT_SIZE * depth; i < len; ++i) {
            sb.append(' ');
        }

        return sb.toString();
    }

    private void depthAppend(StringBuilder sb, int depth, String s) {
        sb.append(getDepth(depth));
        sb.append(s);
    }
}
