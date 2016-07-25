package it.isislab.streamingkway.exceptions;

public class HeuristicNotFound extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8299395204259829060L;

	public HeuristicNotFound() {
		// TODO Auto-generated constructor stub
	}

	public HeuristicNotFound(String message) {
		super(message);
	}

	public HeuristicNotFound(Throwable cause) {
		super(cause);
	}

	public HeuristicNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public HeuristicNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
