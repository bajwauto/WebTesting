package utils;

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
}
