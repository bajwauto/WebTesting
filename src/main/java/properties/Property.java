package properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Property {
	/**
	 * This method is used to read the value of a property from a properties file
	 * 
	 * @param filePath     - path to the properties file
	 * @param propertyName - name of the property
	 * @return - value of the desired property
	 * @throws IOException
	 */
	public static String read(String filePath, String propertyName) throws IOException {
		String propertyValue;
		FileReader reader = new FileReader(filePath);
		Properties properties = new Properties();
		properties.load(reader);
		if (!properties.containsKey(propertyName))
			propertyValue = null;
		else
			propertyValue = properties.getProperty(propertyName);
		reader.close();
		return propertyValue;
	}

	/**
	 * This method is used to read the value of a property from a XML file
	 * 
	 * @param xmlFilePath  - path to the XML file
	 * @param propertyName - name of the property
	 * @return - value of the desired property
	 * @throws IOException
	 */
	public static String readFromXML(String xmlFilePath, String propertyName) throws IOException {
		String propertyValue;
		FileInputStream fis = new FileInputStream(xmlFilePath);
		Properties properties = new Properties();
		properties.loadFromXML(fis);
		if (!properties.containsKey(propertyName))
			propertyValue = null;
		else
			propertyValue = properties.getProperty(propertyName);
		fis.close();
		return propertyValue;
	}

	/**
	 * This method is used to copy the contents of a properties file to a new XML
	 * file
	 * 
	 * @param propertiesFilePath - path to the properties file
	 * @param xmlFilePath        - path to the XML file
	 * @throws IOException
	 */
	public static void convertToXML(String propertiesFilePath, String xmlFilePath) throws IOException {
		FileReader reader = new FileReader(propertiesFilePath);
		FileOutputStream fos = new FileOutputStream(xmlFilePath);
		Properties properties = new Properties();
		properties.load(reader);
		properties.storeToXML(fos, "Converted");
		fos.close();
		reader.close();
	}
}
