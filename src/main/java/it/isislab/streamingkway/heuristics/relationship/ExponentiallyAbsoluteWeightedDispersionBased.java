package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyAbsoluteWeightedDispersionBased extends AbstractAbsoluteDispersionBased implements ExponentiallyWeightedHeuristic {

	public ExponentiallyAbsoluteWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}
	
	public ExponentiallyAbsoluteWeightedDispersionBased(Double A, Double B, Double C,boolean parallel) {
		super(A, B, C, parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Exponentially " + super.getHeuristicName() + (parallel ? " Parallel" : "");
	}

}
