package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearAbsWeightedDispersionBased extends AbstractAbsDispersionBased implements LinearWeightedHeuristic {

	public LinearAbsWeightedDispersionBased(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Linear" + super.getHeuristicName() + (parallel ? " Parallel" : "");
	}

}
