package xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Xml {
	/**
	 * This method is used to extract data from a string containing XML data
	 * 
	 * @param xmlData - String containing the XML data
	 * @param xPath   - xPath query to extract the desired data from the XML String
	 * @return - desired data extracted from the XML string as per the xPath Query
	 */
	public static List<String> readFromString(String xmlData, String xPath) {
		List<String> data = new ArrayList<String>();
		try {
			Document document = getXMLDocument(xmlData);
			NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().compile(xPath).evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
				data.add(nodes.item(i).getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * This method is used to extract data from a XML file
	 * 
	 * @param filePath - path to the XML file
	 * @param xPath    - xPath query to extract the desired data from the XML file
	 * @return - desired data extracted from the XML file as per the xPath Query
	 * @throws IOException
	 */
	public static List<String> read(String filePath, String xPath) throws IOException {
		List<String> data = new ArrayList<String>();
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
			NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().compile(xPath).evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
				data.add(nodes.item(i).getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * This method is used to convert a String containing XML data into an XML
	 * Document
	 * 
	 * @param xml - String containing the XML data
	 * @return - the XML document
	 */
	private static Document getXMLDocument(String xml) {
		Document document = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.parse(new InputSource(new StringReader(xml)));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return document;
	}
}
