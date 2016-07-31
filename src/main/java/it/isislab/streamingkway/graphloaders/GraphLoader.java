package it.isislab.streamingkway.graphloaders;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;

public interface GraphLoader  {

	public static final String CONNECTED_COMPONENT_ATTR = "connected-component";
	
	public GraphPartitionator getGraphPartitionator();
	public int getNodeNumbers();
	public int getEdgeNumbers();
	public void run();
}
