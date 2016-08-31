package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class UnweightedRandomizedGreedy extends AbstractRandomizedGreedy implements UnweightedHeuristic {
	

	public UnweightedRandomizedGreedy(boolean parallel) {
		super(parallel);
	}


	public Integer getIndex(PartitionMap partitionMap, Node n) {
		return super.getIndex(partitionMap, n);
	}

	
	public String getHeuristicName() {
		return "Unweighted "+super.getHeuristicName()+ (parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

}
