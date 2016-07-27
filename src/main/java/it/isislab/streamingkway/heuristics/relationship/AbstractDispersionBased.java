package it.isislab.streamingkway.heuristics.relationship;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractDispersionBased implements SGPHeuristic, WeightedHeuristic {

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		//TODO
		return null;
	}

	public abstract String getHeuristicName();
	public abstract Double getWeight(Double intersectNumber, Integer c);
	
	
}
