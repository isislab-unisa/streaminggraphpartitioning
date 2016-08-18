package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public interface SGPHeuristic {
	

	public Integer getIndex(PartitionMap partitionMap, Node n);
	public String getHeuristicName();
}
