package it.isislab.streamingkway.heuristics.my;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyCompAbsWeightedDispersionBased extends AbstractCompleteAbsDispersionBased implements ExponentiallyWeightedHeuristic {

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Exponentially Weighted Dispersion Based";
	}

}
