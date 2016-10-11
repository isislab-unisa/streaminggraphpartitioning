package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearAbsoluteWeightedDispersionBased extends AbstractAbsoluteDispersionBased implements LinearWeightedHeuristic{

	public LinearAbsoluteWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}
	
	public LinearAbsoluteWeightedDispersionBased(Double A, Double B, Double C, boolean parallel) {
		super(A, B, C, parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Linear "+ super.getHeuristicName() +(parallel ? " Parallel" : "");
	}

}
