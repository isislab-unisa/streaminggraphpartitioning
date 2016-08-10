package it.isislab.streamingkway.exceptions;

/**
 * 
 * @author Dario Di Pasquale
 * 
 * This exception is thrown when the heuristic's number does not exist.
 */
public class HeuristicNotFound extends RuntimeException {

	private static final long serialVersionUID = -8299395204259829060L;

	public HeuristicNotFound() {
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
