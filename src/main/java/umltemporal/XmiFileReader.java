package umltemporal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XmiFileReader {

	private List<UmlActivityDiagram> activityDiagrams;
	private DocumentBuilderFactory docFactory;
	private File file;

	public XmiFileReader(File xmiFile) {
		file = xmiFile;
		docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringComments(true);
		docFactory.setIgnoringElementContentWhitespace(false);
		docFactory.setValidating(false);
	}

	public void parse() throws Exception {
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
		NodeList diagElems = doc.getElementsByTagName("packagedElement");

		activityDiagrams = new ArrayList<UmlActivityDiagram>();

		for (int i = 0; i < diagElems.getLength(); ++i) {
			Node elem = diagElems.item(i);
			Node typeAttr = elem.getAttributes().getNamedItem("xmi:type");
			if (typeAttr != null && typeAttr.getNodeValue().equals("uml:Activity")) {
				UmlActivityDiagram activityDiag = parseDiagram(elem);
				activityDiagrams.add(activityDiag);
			}
		}
	}

	public List<UmlActivityDiagram> getActivityDiagrams() {
		return activityDiagrams;
	}

	private UmlActivityDiagram parseDiagram(final Node diagramNode) {
		List<UmlNode> nodes = new ArrayList<UmlNode>();
		List<UmlEdge> edges = new ArrayList<UmlEdge>();

		NodeList childNodes = diagramNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isNodeElement(childNode)) {
				nodes.add(buildUmlNode(childNode));
			} else if (isEdgeElement(childNode)) {
				edges.add(builUmlEdge(childNode));
			}
		}

		return new UmlActivityDiagram(edges, nodes);
	}

	private boolean isEdgeElement(Node node) {
		if (!node.getNodeName().equals("edge")) {
			return false;
		}

		Node attr = node.getAttributes().getNamedItem("xmi:type");
		return attr != null && ("uml:ControlFlow").equals(attr.getNodeValue());
	}

	private boolean isNodeElement(Node node) {
		return ("node").equals(node.getNodeName());
	}

	private UmlEdge builUmlEdge(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		String id = attributes.getNamedItem("xmi:id").getNodeValue();
		String sourceID = attributes.getNamedItem("source").getNodeValue();
		String targetID = attributes.getNamedItem("target").getNodeValue();

		NodeList childNodes = node.getChildNodes();
		String guard = null;
		for (int i = 0; i < childNodes.getLength(); ++i) {
			if ("guard".equals(childNodes.item(i).getNodeName())) {
				guard = getEdgeGuardValue(childNodes.item(i));
				break;
			}
		}

		UmlEdge edge = new UmlEdge(id, sourceID, targetID);
		if (guard != null) {
			edge.setGuard(guard);
		}

		return edge;
	}

	private String getEdgeGuardValue(Node guardNode) {
		NodeList childNodes = guardNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			if ("body".equals(childNodes.item(i).getNodeName())) {
				return childNodes.item(i).getFirstChild().getNodeValue();
			}
		}

		return null;
	}

	private UmlNode buildUmlNode(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		String id = attributes.getNamedItem("xmi:id").getNodeValue();
		NodeType nodeType = NodeType.fromString(attributes.getNamedItem("xmi:type").getNodeValue());

		UmlNode umlNode = new UmlNode(id, nodeType);

		Node outAttr = attributes.getNamedItem("outgoing");
		if (outAttr != null) {
			umlNode.setOutgoing(Arrays.asList(outAttr.getNodeValue().split("\\s+")));
		}

		Node inAttr = attributes.getNamedItem("incoming");
		if (inAttr != null) {
			umlNode.setIncoming(Arrays.asList(inAttr.getNodeValue().split("\\s+")));
		}

		Node nameAttr = attributes.getNamedItem("name");
		if (nameAttr != null) {
			umlNode.setName(nameAttr.getNodeValue());
		}

		return umlNode;
	}
}
