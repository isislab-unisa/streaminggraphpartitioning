package it.isislab.streamingkway.heuristics.my;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyCompNormWeightedDispersionBased extends AbstractCompleteNormDispersionBased implements ExponentiallyWeightedHeuristic {

	public ExponentiallyCompNormWeightedDispersionBased() {super();}


	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Exponentially Normalized Weighted Dispersion Based";
	}

}
