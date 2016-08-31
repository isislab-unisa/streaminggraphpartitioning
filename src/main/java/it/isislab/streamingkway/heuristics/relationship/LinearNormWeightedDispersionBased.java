package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearNormWeightedDispersionBased extends AbstractNormDispersionBased implements LinearWeightedHeuristic{

	public LinearNormWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}
	
	public LinearNormWeightedDispersionBased(Double A, Double B, Double C, boolean parallel) {
		super(A, B, C, parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Linear "+ super.getHeuristicName() +(parallel ? " Parallel" : "");
	}

}
