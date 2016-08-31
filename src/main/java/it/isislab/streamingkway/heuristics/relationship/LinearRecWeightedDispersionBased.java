package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearRecWeightedDispersionBased extends AbstractRecursiveDispersionBased implements LinearWeightedHeuristic {
	
	public LinearRecWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}

	public String getHeuristicName() {
		return "Linear "+ super.getHeuristicName() +(parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

}
