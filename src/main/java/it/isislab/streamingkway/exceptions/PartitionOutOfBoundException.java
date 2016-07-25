package it.isislab.streamingkway.exceptions;

public class PartitionOutOfBoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 496988366610157422L;
	public PartitionOutOfBoundException() {super();}
	public PartitionOutOfBoundException(String message) {super(message);}
	public PartitionOutOfBoundException(String message, Throwable cause) {super(message,cause);}
	public PartitionOutOfBoundException(Throwable cause) {super(cause);}
	
}
