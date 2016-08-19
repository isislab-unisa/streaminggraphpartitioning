package it.isislab.streamingkway.heuristics.my;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearCompAbsWeightedDispersionBased extends AbstractCompleteAbsDispersionBased implements LinearWeightedHeuristic {

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Linear Complete Weighted Absolute Dispersion Based";
	}

}
