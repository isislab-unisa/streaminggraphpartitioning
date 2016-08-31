package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class LinearWeightedRandomizedGreedy extends AbstractRandomizedGreedy implements LinearWeightedHeuristic {
	

	public LinearWeightedRandomizedGreedy(boolean parallel) {
		super(parallel);
	}


	public Integer getIndex(PartitionMap partitionMap, Node n) {
		return super.getIndex(partitionMap, n);
	}

	
	public String getHeuristicName() {
		return "Linear "+super.getHeuristicName()+ (parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

}
