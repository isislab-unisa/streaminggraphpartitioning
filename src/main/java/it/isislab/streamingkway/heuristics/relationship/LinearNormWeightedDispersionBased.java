package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearNormWeightedDispersionBased extends AbstractNormDispersionBased implements LinearWeightedHeuristic{

	public LinearNormWeightedDispersionBased() {super();}
	
	public LinearNormWeightedDispersionBased(Double A, Double B, Double C) {
		super(A, B, C);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Linear Normalized Weighted Dispersion Based";
	}

}
