package it.isislab.streamingkway.heuristics.my;

import it.isislab.streamingkway.heuristics.weight.UnweightedHeuristic;

public class UnweightedCompNormDispersionBased extends AbstractCompleteNormDispersionBased implements UnweightedHeuristic {

	public UnweightedCompNormDispersionBased() {super();}
	

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightUn(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Unweighted Complete Normalized Dispersion Based";
	}

}
