package commons.exception;

public class NotExistentPropertyException extends Exception {

	private static final long serialVersionUID = 2754826431102282737L;

	public NotExistentPropertyException() {
		
	}

	public NotExistentPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotExistentPropertyException(String message) {
		super(message);
	}

	public NotExistentPropertyException(Throwable cause) {
		super(cause);
	}
}
