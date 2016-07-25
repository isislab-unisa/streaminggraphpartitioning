package it.isislab.streamingkway.heuristics.my;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class ParallelUnweightedDeterministicGreedy extends ParallelAbstractDeterministicGreedy {

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		return super.getIndex(g, partitionMap, n);
	}

	public String getHeuristicName() {
		return "Unweighted Deterministic Greedy";
	}

	public Double getWeight(Double intersectNodes, Integer c) {
		return 1.0;
	}

}
