package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyRecWeightedDispersionBased extends AbstractRecursiveDispersionBased implements ExponentiallyWeightedHeuristic {

	public String getHeuristicName() {
		return "Exponentially Recursive Dispersion Based";
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

}
