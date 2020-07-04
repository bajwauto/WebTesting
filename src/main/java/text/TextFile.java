package text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFile {
	/**
	 * This method is used to read all the data present in a text file
	 * 
	 * @param filePath - path to the text file
	 * @return List of Strings containing all the data present in the text file
	 */
	public static List<String> read(String filePath) throws IOException {
		String currentLine;
		List<String> text = new ArrayList<String>();
		File file = new File(filePath);
		if (!file.exists())
			text = null;
		else {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			while ((currentLine = reader.readLine()) != null)
				text.add(currentLine);
			reader.close();
		}
		return text;
	}

	/**
	 * This method is used to read a line from a text file
	 * 
	 * @param filePath - path to the text file
	 * @param LineNo   - line number to be read
	 * @return String containing the text present at the specified line number in
	 *         the file
	 */
	public static String read(String filePath, int lineNo) throws IOException {
		List<String> text = read(filePath);
		String desiredText;
		if (text == null)
			desiredText = null;
		else if (text.size() < lineNo)
			desiredText = null;
		else
			desiredText = text.get(lineNo - 1);
		return desiredText;
	}

	/**
	 * This method is used to write data to a file
	 * 
	 * @param filePath  - path to the file
	 * @param data      - data/text to be written to the file
	 * @param overwrite - true to overwrite the existing contents, false to append
	 *                  the text at the end of the file
	 * @throws IOException
	 */
	public static void write(String filePath, String data, boolean overwrite) throws IOException {
		File file = new File(filePath);
		FileWriter fileWriter = new FileWriter(file, !overwrite);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		writer.write(data);
		writer.close();
	}

	/**
	 * This method is used to copy the text contents from one file to another
	 * 
	 * @param sourcePath      - path to the source file to be read
	 * @param destinationPath - path to the destination file to where the contents
	 *                        are to be copied
	 * @throws IOException
	 */
	public static void copyContents(String sourcePath, String destinationPath) throws IOException {
		File source = new File(sourcePath);
		File destination = new File(destinationPath);
		FileReader fileReader = new FileReader(source);
		FileWriter fileWriter = new FileWriter(destination);
		BufferedReader reader = new BufferedReader(fileReader);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		reader.transferTo(writer);
		writer.close();
		reader.close();
	}
}
