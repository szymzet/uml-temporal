package umltemporal;

import java.util.ArrayList;
import java.util.List;

public class Expression {
	private String name;
	private List<Expression> args;

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