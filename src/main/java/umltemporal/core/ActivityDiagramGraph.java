package umltemporal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityDiagramGraph {

    private UmlNode initialNode;
    private Map<String, UmlElement> elements;

    public UmlNode getInitialNode() {
        return initialNode;
    }

    public ActivityDiagramGraph(UmlActivityDiagram umlDiagram) {
        elements = new HashMap<String, UmlElement>();

        for (UmlNode node : umlDiagram.getNodes()) {
            elements.put(node.getID(), node);

            if (node.getNodeType() == NodeType.INITIAL_NODE) {
                initialNode = node;
            }
        }

        for (UmlEdge edge : umlDiagram.getEdges()) {
            elements.put(edge.getID(), edge);
        }
    }

    public List<UmlNode> getOutgoing(UmlNode node) {
        List<UmlNode> result = new ArrayList<UmlNode>();
        List<String> ids = node.getOutgoing();
        for (String edgeID : ids) {
            UmlEdge e = (UmlEdge) elements.get(edgeID);
            result.add((UmlNode) elements.get(e.getTargetID()));
        }

        return result;
    }

    public UmlEdge getEdge(UmlNode startNode, UmlNode endNode) {
        for (String outID : startNode.getOutgoing()) {
            for (String inID : endNode.getIncoming()) {
                if (outID.equals(inID) && outID != null) {
                    return (UmlEdge) elements.get(inID);
                }
            }
        }

        return null;
    }
}
