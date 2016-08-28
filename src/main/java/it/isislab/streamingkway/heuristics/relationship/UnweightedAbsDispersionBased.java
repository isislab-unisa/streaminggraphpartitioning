package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedAbsDispersionBased extends AbstractAbsDispersionBased implements UnweightedHeuristic {

	public UnweightedAbsDispersionBased(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted Absolute Dispersion Based"+ (parallel ? " Parallel" : "");
	}

}
