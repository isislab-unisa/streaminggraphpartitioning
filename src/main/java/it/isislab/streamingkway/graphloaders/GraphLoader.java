package it.isislab.streamingkway.graphloaders;

import java.io.IOException;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;

/**
 * @author Dario Di Pasquale
 * 
 *	An interface that contains general purpose methods for loading and partitioning a graph.
 *	
 *	This is the central interface of KWAYSGP and define methods for start the partitioning of the preloaded
 *  input graph and methods for retrieve informations about the graph.
 *
 *{@value CONNECTED_COMPONENT_ATTR}	The attribute to attach to the node in order to retrieve the connected
 *component to which it belongs.
 */
public interface GraphLoader  {
	
	public static final String CONNECTED_COMPONENT_ATTR = "connected-component";
	
	/**
	 * Get the GraphPartitionator of this GraphLoader.
	 * @return {@link GraphPartitionator} the graph partitionator
	 */
	public GraphPartitionator getGraphPartitionator();
	/**
	 * Get the total number of nodes that the graph should contains. This number is indicated in the first
	 * line of the input file according to METIS graph file format.
	 *  
	 * @return an integer containing the number of nodes that the graph should have
	 * @link http://glaros.dtc.umn.edu/gkhome/views/metis
	 */
	public int getNodeNumbers();
	/**
	 * Get the total number of edges that the graph should contains. This number is indicated in the first
	 * line of the input file according to METIS graph file format.
	 *  
	 * @return an integer containing the number of edges that the graph should have
	 * @link http://glaros.dtc.umn.edu/gkhome/views/metis
	 */
	public int getEdgeNumbers();

	/**
	 * Run the partitioning of the graph given in input according to the specified graph traversing order and
	 * the given heuristic. 
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public void runPartition() throws NumberFormatException, IOException;
	
	public Long getPartitioningTime();
}
