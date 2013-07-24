package umltemporal.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

public class TemporalGenerator {

    private ActivityDiagramGraph graph;
    private HashSet<UmlNode> alreadyVisited;
    private ExpressionFactory expFactory;

    public TemporalGenerator(ActivityDiagramGraph graph) {
        this.graph = graph;
        alreadyVisited = new HashSet<UmlNode>();
        expFactory = new ExpressionFactory();
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

            // ending-type of nodes
            case ACTIVITY_FINAL_NODE:
            case JOIN_NODE: {
                nextNode = joinMergeFinal(node, stack);
                if (nextNode == null) {
                    assert stack.size() <= 1;
                    return null;
                } else {
                    return nextNode;
                }
            }
            // this is a node starting a Loop (in first visit) or an end for loop/branch
            case MERGE_NODE:
                if (isMergeAssociatedWithDecision(node) && !alreadyVisited.contains(node)) {
                    // skip this MERGE_NODE and go straight to decision - this is
                    // gonna be loop. Do this only for the first visit
                    alreadyVisited.add(node);
                    // build from associated DECISION_NODE
                    return build(graph.getOutgoing(node).get(0), stack);
                }
                // otherwise just end of a branch
                return joinMergeFinal(node, stack);

            case DECISION_NODE:
                nextNode = decision(node, stack);
                return build(nextNode, stack);

            case FORK_NODE:
                nextNode = fork(node, stack);
                return build(nextNode, stack);

            case OPAQUE_ACTION:
                nextNode = graph.getOutgoing(node).get(0);
                stack.push(expFactory.get("'" + node.getName() + "'"));
                return build(nextNode, stack);

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
     * requires that both outgoing edges have guards like "Action 1", "NOT
     * Action 1".
     *
     * @param node
     * @param stack
     * @return Next node after identified pattern.
     */
    private UmlNode decision(UmlNode node, Deque<Expression> stack) {
        UmlNode right = getFalse(node);
        UmlNode left = getTrue(node);
        Deque<Expression> leftStack = new ArrayDeque<Expression>();

        // next node after the 'positive' subexpression
        UmlNode leftNextNode = build(left, leftStack);

        //
        // is it a loop?
        //

        // in a loop the positive branch should loop back
        if (leftNextNode == right) { // it depends on the fact: joinMergeFinal assures that next(MERGE node) == getFalse(MERGE node)
            Expression loop = expFactory.get(ExpressionType.LOOP);
            loop.addArg(stack.pop());
            loop.addArg(getGuardExpression(node, left));
            loop.addArg(leftStack.pop());
            assert leftStack.isEmpty();
            stack.push(loop);
            return right;
        }

        //
        // it is a branching
        //

        Deque<Expression> rightStack = new ArrayDeque<Expression>();
        UmlNode rightNextNode = build(right, rightStack);

        // they must end at the same merging node
        assert rightNextNode == leftNextNode;

        Expression branch = expFactory.get(ExpressionType.BRANCHING);
        branch.addArg(stack.pop());
        branch.addArg(leftStack.pop());
        branch.addArg(rightStack.pop());
        stack.push(branch);
        return leftNextNode;
    }

    private Expression getGuardExpression(UmlNode startNode, UmlNode endNode) {
        String guard = graph.getEdge(startNode, endNode).getGuard();
        assert guard != null;
        return expFactory.get("'" + guard + "'");
    }

    /**
     * Ends a phase in traversing. After a call to this method the stack has
     * only one or zero elements.
     *
     * @param node UmlNode representing on of: ACTIVITY_FINAL_NODE, JOIN_NODE,
     * MERGE_NODE
     * @param stack
     * @return null if is ACTIVITY_FINAL_NODE, first node from false
     * subexpression for loops, next node otherwise
     */
    private UmlNode joinMergeFinal(UmlNode node, Deque<Expression> stack) {
        if (stack.size() > 1) {
            stack.push(stackToSeq(stack));
        }

        List<UmlNode> outgoing = graph.getOutgoing(node);

        // special treatment for finalizing in loop - skip to associated decision and
        // go to the false branch
        if (node.getNodeType() == NodeType.MERGE_NODE && isMergeAssociatedWithDecision(node)) {
            return getFalse(outgoing.get(0));
        }
        return outgoing.isEmpty() ? null : outgoing.get(0);
    }

    /**
     * Takes everything from the stack and creates a sequence expression.
     *
     * @param stack Stack that will be emptied.
     * @return Created Seq expression
     */
    private Expression stackToSeq(Deque<Expression> stack) {
        assert stack.size() > 0;
        Expression right = stack.pop();
        while (!stack.isEmpty()) {
            Expression left = stack.pop();
            Expression newRight = expFactory.get(ExpressionType.SEQUENCE);
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

        Expression concur = expFactory.get(ExpressionType.CONCURRENCY);
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
