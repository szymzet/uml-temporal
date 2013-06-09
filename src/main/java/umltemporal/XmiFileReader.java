package umltemporal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XmiFileReader {

	private File file;
	private List<UmlActivityDiagram> activityDiagrams;
	private DocumentBuilderFactory docFactory;

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
			if (typeAttr != null
					&& typeAttr.getNodeValue().equals("uml:Activity")) {
				UmlActivityDiagram activityDiag = parseDiagram(elem);
				activityDiagrams.add(activityDiag);
			}
		}
	}

	private UmlActivityDiagram parseDiagram(final Node diagramNode) {
		return new UmlActivityDiagram(parseDiagramEdges(diagramNode),
				parseDiagramNodes(diagramNode));
	}

	private List<UmlNode> parseDiagramNodes(final Node diagramNode) {
		List<UmlNode> nodes = new ArrayList<UmlNode>();

		NodeList childNodes = diagramNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeName().equals("node")) {
				nodes.add(new UmlNode("id", NodeType.INITIAL_NODE));
			}
		}

		return nodes;
	}

	private List<UmlEdge> parseDiagramEdges(final Node diagramNode) {
		List<UmlEdge> edges = new ArrayList<UmlEdge>();
		NodeList childNodes = diagramNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeName().equals("edge")) {
				edges.add(new UmlEdge("id", "asdf", "cv"));
			}
		}

		return edges;
	}

	public List<UmlActivityDiagram> getActivityDiagrams() {
		return activityDiagrams;
	}
}
