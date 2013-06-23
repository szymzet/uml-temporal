package umltemporal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TemporalGenerator {

	private ActivityDiagramGraph graph;

	public TemporalGenerator(ActivityDiagramGraph graph) {
		this.graph = graph;
	}

	public Expression buildExpression() {
		UmlNode initialNode = graph.getInitialNode();
		UmlNode nextNode = graph.getOutgoing(initialNode).get(0);
		Deque<Expression> stack = new ArrayDeque<Expression>();
		while (nextNode != null) {
			nextNode = build(nextNode, stack);
		}

		assert stack.size() == 1;
		return stack.pop();
	}

	/**
	 * 
	 * @param node
	 * @param stack
	 * @return null if end of graph, next node otherwise
	 */
	private UmlNode build(UmlNode node, Deque<Expression> stack) {

		UmlNode nextNode;
		switch (node.getNodeType()) {

		case ACTIVITY_FINAL_NODE:
		case JOIN_NODE:
			nextNode = joinMergeFinal(node, stack);
			if (nextNode == null) {
				assert stack.size() <= 1;
				return null;
			} else {
				return build(nextNode, stack);
			}
		case MERGE_NODE:
			if (isMergeAssociatedWithDecision(node)) {
				// skip this MERGE_NODE and go straight to decision - this is
				// gonna be loop
				return graph.getOutgoing(node).get(0);
			}
			// otherwise just end of a branch
			nextNode = joinMergeFinal(node, stack);
			return build(nextNode, stack);

		case DECISION_NODE:
			return decision(node, stack);

		case FORK_NODE:
			return fork(node, stack);

		case OPAQUE_ACTION:
			nextNode = graph.getOutgoing(node).get(0);
			stack.push(new Expression("'" + node.getName() + "'"));
			return nextNode;

		default:
			throw new IllegalStateException("all of the node types should be cased, why am I here?!");
		}
	}

	/**
	 * Get the child with 'positive' guard - aka the branch which is True
	 * 
	 * @return
	 */
	private UmlNode getTrue(UmlNode node) {
		List<UmlNode> outgoing = graph.getOutgoing(node);

		// assuming there are only 2 nodes in outgoing
		assert outgoing.size() == 2;

		UmlNode falseNode = getFalse(node);
		if (outgoing.get(0) == falseNode) {
			return outgoing.get(1);
		}

		return outgoing.get(0);
	}

	/**
	 * Get the child with 'negative' guard - aka the branch which is False (has
	 * NOT in the beggining)
	 * 
	 * @return
	 */
	private UmlNode getFalse(UmlNode node) {
		List<UmlNode> outgoing = graph.getOutgoing(node);
		for (UmlNode next : outgoing) {
			String guard = graph.getEdge(node, next).getGuard();
			if (guard != null && guard.matches("^\\s*[nN][oO][tT]\\s+.*")) {
				return next;
			}
		}

		return null;
	}

	/**
	 * Decision node logic. Can result in Loop or Branch expressions. It
	 * requires that both outgoing edges have guards like "Action 1",
	 * "NOT Action 1".
	 * 
	 * @param node
	 * @param stack
	 * @return Next node after identified pattern.
	 */
	private UmlNode decision(UmlNode node, Deque<Expression> stack) {
		UmlNode right = getFalse(node);
		UmlNode left = getTrue(node);
		Deque<Expression> leftStack = new ArrayDeque<Expression>();
		UmlNode leftNextNode = build(left, leftStack);

		// is it a loop?
		// in a loop the positive branch should go back to the decision node
		// it should end on a merge, so we check weather merge is associated
		// with the same decision
		List<UmlNode> leftNextOutgoing = graph.getOutgoing(leftNextNode);
		if (leftNextOutgoing.size() == 1 && leftNextOutgoing.get(0) == node) {
			Expression loop = new Expression("Loop");
			loop.addArg(stack.pop());
			loop.addArg(getGuardExpression(node, left));
			loop.addArg(leftStack.pop());
			stack.push(loop);
			return right;
		}

		// it is a branching
		Deque<Expression> rightStack = new ArrayDeque<Expression>();
		UmlNode rightNextNode = build(right, rightStack);

		// they must end at the same merging node
		assert rightNextNode == leftNextNode;

		Expression branch = new Expression("Branch");
		branch.addArg(stack.pop());
		branch.addArg(leftStack.pop());
		branch.addArg(rightStack.pop());
		stack.push(branch);
		return leftNextNode;
	}

	private Expression getGuardExpression(UmlNode startNode, UmlNode endNode) {
		String guard = graph.getEdge(startNode, endNode).getGuard();
		assert guard != null;
		return new Expression("'" + guard + "'");
	}

	/**
	 * Ends a phase in traversing. After a call to this method the stack has
	 * only one or zero elements.
	 * 
	 * @param node
	 *            UmlNode representing on of: ACTIVITY_FINAL_NODE, JOIN_NODE,
	 *            MERGE_NODE
	 * @param stack
	 * @return null if is ACTIVITY_FINAL_NODE, next node otherwise
	 */
	private UmlNode joinMergeFinal(UmlNode node, Deque<Expression> stack) {
		if (stack.size() > 1) {
			stack.push(stackToSeq(stack));
		}

		List<UmlNode> outgoing = graph.getOutgoing(node);
		return outgoing.isEmpty() ? null : outgoing.get(0);
	}

	/**
	 * Takes everything from the stack and creates a sequence expression.
	 * 
	 * @param stack
	 *            Stack that will be emptied.
	 * @return Created Seq expression
	 */
	private Expression stackToSeq(Deque<Expression> stack) {
		assert stack.size() > 0;
		Expression right = stack.pop();
		while (!stack.isEmpty()) {
			Expression left = stack.pop();
			Expression newRight = new Expression("Seq");
			newRight.addArg(left);
			newRight.addArg(right);
			right = newRight;
		}

		assert stack.isEmpty();
		return right;
	}

	/**
	 * Results in a creation of the Concur pattern.
	 * 
	 * @param node
	 * @param stack
	 * @return Next node after identified patter.
	 */
	private UmlNode fork(UmlNode node, Deque<Expression> stack) {
		List<UmlNode> outgoing = graph.getOutgoing(node);
		assert outgoing.size() == 2;

		UmlNode left = outgoing.get(0);
		Deque<Expression> leftStack = new ArrayDeque<Expression>();
		UmlNode leftNextNode = build(left, leftStack);

		UmlNode right = outgoing.get(1);
		Deque<Expression> rightStack = new ArrayDeque<Expression>();
		UmlNode rightNextNode = build(right, rightStack);

		Expression concur = new Expression("Concur");
		concur.addArg(stack.pop());
		concur.addArg(leftStack.pop());
		concur.addArg(rightStack.pop());
		stack.push(concur);

		assert leftNextNode == rightNextNode;
		return leftNextNode;
	}

	private boolean isMergeAssociatedWithDecision(UmlNode mergeNode) {
		List<UmlNode> outgoing = graph.getOutgoing(mergeNode);
		return (outgoing.size() == 1 && outgoing.get(0).getNodeType() == NodeType.DECISION_NODE);
	}
}
