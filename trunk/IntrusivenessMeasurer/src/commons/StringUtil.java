package commons;

public class StringUtil {
	public static boolean isNumeric(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isDigit(string.charAt(i)) && !(string.charAt(i) == '.') && !(string.charAt(i) == '-')) {
				return false;
			}
		}
		return true;
	}
}
