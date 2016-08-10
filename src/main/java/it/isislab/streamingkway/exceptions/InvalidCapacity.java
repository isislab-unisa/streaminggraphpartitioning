package it.isislab.streamingkway.exceptions;

/**
 * @author Dario Di Pasquale
 *
 * This exception is thrown when the capacity is less than n/k, where n is the number of the nodes
 * in the graph and k is the number of the partitions, so the graph cannot be fully partitioned. 
 */
public class InvalidCapacity extends RuntimeException {

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
