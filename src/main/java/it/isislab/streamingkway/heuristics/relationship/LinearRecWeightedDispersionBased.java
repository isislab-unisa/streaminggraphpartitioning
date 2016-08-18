package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearRecWeightedDispersionBased extends AbstractRecursiveDispersionBased implements LinearWeightedHeuristic {
	
	public String getHeuristicName() {
		return "Linear Recursive Dispersion Based";
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

}
