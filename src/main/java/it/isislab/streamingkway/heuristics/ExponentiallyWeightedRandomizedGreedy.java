package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class ExponentiallyWeightedRandomizedGreedy extends AbstractRandomizedGreedy {
	

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		return super.getIndex(g, partitionMap, n);
	}

	
	public String getHeuristicName() {
		return "Exponentially Weighted Randomized Greedy";
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - Math.exp(partitionSize - c);
	}

}
