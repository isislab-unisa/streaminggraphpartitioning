package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedNormalizedDispersionBased extends AbstractNormalizedDispersionBased implements UnweightedHeuristic {

	public UnweightedNormalizedDispersionBased(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted" + super.getHeuristicName() + (parallel ? " Parallel" : "");
	}

}
