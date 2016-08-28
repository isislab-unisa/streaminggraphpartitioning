package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class LinearWeightedDeterministicGreedy extends AbstractDeterministicGreedy implements LinearWeightedHeuristic {


	public LinearWeightedDeterministicGreedy(boolean parallel) {
		super(parallel);
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		return super.getIndex(partitionMap, n);
	}

	public String getHeuristicName() {
		return "Linear Weighted Deterministic Greedy"+ (parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

}
