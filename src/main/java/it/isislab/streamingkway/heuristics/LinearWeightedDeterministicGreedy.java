package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class LinearWeightedDeterministicGreedy extends AbstractDeterministicGreedy {


	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		return super.getIndex(g, partitionMap, n);
	}

	public String getHeuristicName() {
		return "Linear Weighted Deterministic Greedy";
	}

	public Double getWeight(Double intersectNodes, Integer c) {
		return 1-intersectNodes/c;
	}

}
