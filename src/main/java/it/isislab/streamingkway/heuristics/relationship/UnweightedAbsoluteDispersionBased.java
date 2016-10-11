package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedAbsoluteDispersionBased extends AbstractAbsoluteDispersionBased implements UnweightedHeuristic {

	public UnweightedAbsoluteDispersionBased(boolean parallel) {
		super(parallel);
	}
	
	public UnweightedAbsoluteDispersionBased(Double A, Double B, Double C, boolean parallel) {
		super(A, B, C, parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted "+super.getHeuristicName()+ (parallel ? " Parallel" : "");
	}

}
