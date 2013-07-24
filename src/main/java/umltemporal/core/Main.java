package umltemporal.core;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory
            .newInstance();
        docFactory.setIgnoringComments(true);
        docFactory.setIgnoringElementContentWhitespace(false);
        docFactory.setValidating(false);

        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            File f = new File(args[0]);
            Document doc = docBuilder.parse(f);
            traverse(doc.getDocumentElement());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void p(String s) {
        System.out.println(s);
    }

    public static void traverse(Node node) {
        p("name  : [" + node.getNodeName() + "]");
        p("value : [" + node.getNodeValue() + "]");

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            p("attributes: ");
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                p("    " + attr.getNodeName() + "=" + attr.getNodeValue());
            }
        }

        p("");

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            traverse(childNodes.item(i));
        }
    }
}
