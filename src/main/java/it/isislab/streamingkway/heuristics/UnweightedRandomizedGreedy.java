package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class UnweightedRandomizedGreedy extends AbstractRandomizedGreedy {
	

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		return super.getIndex(g, partitionMap, n);
	}

	
	public String getHeuristicName() {
		return "Unweighted Randomized Greedy";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1.0;
	}

}
