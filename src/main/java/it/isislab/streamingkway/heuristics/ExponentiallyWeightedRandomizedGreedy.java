package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class ExponentiallyWeightedRandomizedGreedy extends AbstractRandomizedGreedy implements ExponentiallyWeightedHeuristic {
	

	public ExponentiallyWeightedRandomizedGreedy(boolean parallel) {
		super(parallel);
	}


	public Integer getIndex(PartitionMap partitionMap, Node n) {
		return super.getIndex(partitionMap, n);
	}

	
	public String getHeuristicName() {
		return "Exponentially Weighted Randomized Greedy"+ (parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}



}
