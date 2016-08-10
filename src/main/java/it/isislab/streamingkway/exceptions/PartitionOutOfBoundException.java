package it.isislab.streamingkway.exceptions;

/**
 * @author Dario Di Pasquale
 *
 * This exception is thrown when the partitionator is trying to insert a node into a partition that is
 * already full.
 */
public class PartitionOutOfBoundException extends RuntimeException {

	private static final long serialVersionUID = 496988366610157422L;
	public PartitionOutOfBoundException() {super();}
	public PartitionOutOfBoundException(String message) {super(message);}
	public PartitionOutOfBoundException(String message, Throwable cause) {super(message,cause);}
	public PartitionOutOfBoundException(Throwable cause) {super(cause);}
	
}
