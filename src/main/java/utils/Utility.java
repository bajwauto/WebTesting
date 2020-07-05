package utils;

import java.io.File;
import java.util.Base64;

public class Utility {
	/**
	 * This method is used to encode a string/text
	 * 
	 * @param textToEncode - string/text to be encoded
	 * @return - the encoded string
	 */
	public static String encode(String textToEncode) {
		return Base64.getEncoder().encodeToString(textToEncode.getBytes());
	}

	/**
	 * This method is used to decode an encoded string/text
	 * 
	 * @param textToDecode - text/string to be decoded
	 * @return - the decoded string
	 */
	public static String decode(String textToDecode) {
		return new String(Base64.getDecoder().decode(textToDecode));
	}

	/**
	 * This method is used to get the path to a file or a folder relative to the
	 * project path
	 * 
	 * @param entityName - name of the folder or file present inside the project
	 *                   structure
	 * @return - relative path of the file or folder w.r.t. project's root
	 */
	private static String getProjectPaths(String entityName) {
		String path = "";
		switch (entityName) {
		case "main":
		case "test":
			path = "src/" + entityName;
			break;
		case "mainjava":
			path = getProjectPaths("main") + "/java";
			break;
		case "mainresources":
			path = getProjectPaths("main") + "/resources";
			break;
		case "testjava":
			path = getProjectPaths("test") + "/java";
			break;
		case "testresources":
			path = getProjectPaths("test") + "/resources";
			break;
		case "or":
			path = getProjectPaths("testresources") + "/OR.properties";
		}
		return path;
	}

	/**
	 * This method is used to get the absolute path to a file or a folder present in
	 * the project structure
	 * 
	 * @param entityName - name of the file or folder whose absolute path is
	 *                   required
	 * @return - absolute path of the file or folder
	 */
	public static String getAbsoluteProjectPaths(String entityName) {
		File file = new File(getProjectPaths(entityName));
		return file.getAbsolutePath();
	}
}
