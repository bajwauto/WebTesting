package utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * This method is used to create a directory in the file system, if it does not
	 * exist
	 * 
	 * @param dirPath - path to the directory to be created
	 * @return - true, iff the directory was created, else false
	 */
	public static boolean createDirectory(String dirPath) {
		File file = new File(dirPath);
		return file.mkdirs();
	}

	/**
	 * This method is used to match and capture groups from a test string based on a
	 * regular expression
	 * 
	 * @param test         - String to be tested against the provided regular
	 *                     expression
	 * @param regexPattern - regular expression pattern
	 * @return - List of a list containing the all the matches and their groups.
	 */
	public static List<List<String>> getMatchesNGroups(String test, String regexPattern) {
		List<List<String>> matchesNGroups = new ArrayList<List<String>>();
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(test);
		while (matcher.find()) {
			List<String> currentMatchNGroups = new ArrayList<String>();
			for (int i = 0; i <= matcher.groupCount(); i++)
				currentMatchNGroups.add(matcher.group(i));
			matchesNGroups.add(currentMatchNGroups);
		}
		return matchesNGroups;
	}

	/**
	 * This method is used to format a date into a given pattern
	 * 
	 * @param date   - date to be formatted
	 * @param format - string containing the desired date format
	 * @return - the formatted date
	 */
	public static String formatDate(Date date, String format) {
		String formattedDate = "";
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
		formattedDate = simpleFormat.format(date);
		return formattedDate;
	}

	/**
	 * This method is used to change the format of a given date to a desired format
	 * 
	 * @param date          - date to be formatted in form of a String
	 * @param initialFormat - initial format of the date passed
	 * @param desiredFormat - desired format of the date
	 * @return - String containing the date in the desired format
	 */
	public static String changeDateFormat(String date, String initialFormat, String desiredFormat) {
		String formattedDate = "";
		SimpleDateFormat initialF = new SimpleDateFormat(initialFormat);
		SimpleDateFormat finalF = new SimpleDateFormat(desiredFormat);
		try {
			formattedDate = finalF.format(initialF.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formattedDate;
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
		case "src":
		case "screenshots":
			path = entityName;
			break;
		case "main":
		case "test":
			path = getProjectPaths("src") + "/" + entityName;
			break;
		case "mainjava":
			path = getProjectPaths("main") + "/java";
			break;
		case "mainresources":
			path = getProjectPaths("main") + "/resources";
			break;
		case "configuration":
			path = getProjectPaths("mainresources") + "/configuration.xml";
			break;
		case "testjava":
			path = getProjectPaths("test") + "/java";
			break;
		case "testresources":
			path = getProjectPaths("test") + "/resources";
			break;
		case "or":
			path = getProjectPaths("testresources") + "/OR.properties";
			break;
		case "testData":
			path = getProjectPaths("testresources") + "/" + entityName;
			break;
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
