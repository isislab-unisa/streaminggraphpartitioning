package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedNormDispersionBased extends AbstractNormDispersionBased implements UnweightedHeuristic {

	public UnweightedNormDispersionBased(boolean parallel) {
		super(parallel);
	}
	
	public UnweightedNormDispersionBased(Double A, Double B, Double C, boolean parallel) {
		super(A, B, C, parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted Normalized Dispersion Based"+ (parallel ? " Parallel" : "");
	}

}
