package commons;

public class Preconditions {
	public static void checkNotNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void check(boolean value, String message) {
		if (!value) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void checkNonNegative(double value, String message) {
		if (value < 0) {
			throw new IllegalArgumentException(message);
		}
	}
}
