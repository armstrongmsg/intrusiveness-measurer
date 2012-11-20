package commons.util;

public class StringUtil {
	/**
	 * Checks if all the characters of the given string are numeric or equal to 
	 * '.' or '-'. It returns false if find a blank character.
	 * @param string
	 * @return
	 */
	// FIXME test it !!!
	public static boolean isNumeric(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isDigit(string.charAt(i)) && !(string.charAt(i) == '.') && !(string.charAt(i) == '-')) {
				return false;
			}
		}
		return true;
	}
	
	// FIXME test it !!!
	public static String concat(String ... strings) {
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			builder.append(string);
		}
		return builder.toString();
	}
}
