package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedNormDispersionBased extends AbstractNormDispersionBased implements UnweightedHeuristic {

	public UnweightedNormDispersionBased() {super();}
	
	public UnweightedNormDispersionBased(Double A, Double B, Double C) {
		super(A, B, C);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted Normalized Dispersion Based";
	}

}
