package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class LinearWeightedRandomizedGreedy extends AbstractRandomizedGreedy {
	

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		return super.getIndex(g, partitionMap, n);
	}

	
	public String getHeuristicName() {
		return "Linear Weighted Randomized Greedy";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - intersectNumber / c;
	}

}
