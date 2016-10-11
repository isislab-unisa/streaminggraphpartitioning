package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyNormalizedWeightedDispersionBased extends AbstractNormalizedDispersionBased implements ExponentiallyWeightedHeuristic {

	public ExponentiallyNormalizedWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Exponentially" + super.getHeuristicName() + (parallel ? " Parallel" : "");
	}

}
