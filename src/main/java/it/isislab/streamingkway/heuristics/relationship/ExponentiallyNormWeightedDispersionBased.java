package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;

public class ExponentiallyNormWeightedDispersionBased extends AbstractNormDispersionBased implements ExponentiallyWeightedHeuristic {

	public ExponentiallyNormWeightedDispersionBased() {super();}
	
	public ExponentiallyNormWeightedDispersionBased(Double A, Double B, Double C) {
		super(A, B, C);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Exponentially Normalized Weighted Dispersion Based";
	}

}
