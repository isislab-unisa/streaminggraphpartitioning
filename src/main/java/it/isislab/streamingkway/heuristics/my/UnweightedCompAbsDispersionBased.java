package it.isislab.streamingkway.heuristics.my;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedCompAbsDispersionBased extends AbstractCompleteAbsDispersionBased implements UnweightedHeuristic {

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted Complete Absolute Dispersion Based";
	}

}
