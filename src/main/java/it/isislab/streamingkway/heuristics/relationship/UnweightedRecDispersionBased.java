package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedRecDispersionBased extends AbstractRecursiveDispersionBased implements UnweightedHeuristic {

	public UnweightedRecDispersionBased(boolean parallel) {
		super(parallel);
	}

	public String getHeuristicName() {
		return "Unweighted "+ super.getHeuristicName() +(parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

}
