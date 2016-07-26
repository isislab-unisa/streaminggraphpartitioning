package it.isislab.streamingkway.exceptions;

public class InvalidCapacity extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCapacity() {
		super();
	}

	public InvalidCapacity(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidCapacity(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCapacity(String message) {
		super(message);
	}

	public InvalidCapacity(Throwable cause) {
		super(cause);
	}

}
