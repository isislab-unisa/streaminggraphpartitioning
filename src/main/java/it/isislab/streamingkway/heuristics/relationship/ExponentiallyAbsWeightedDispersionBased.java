package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyAbsWeightedDispersionBased extends AbstractAbsDispersionBased implements ExponentiallyWeightedHeuristic {

	public ExponentiallyAbsWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Exponentially " + super.getHeuristicName() + (parallel ? " Parallel" : "");
	}

}
