package it.isislab.streamingkway.heuristics.relationship;

public class UnweightedNormDispersionBased extends AbstractAbsDispersionBased {

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1.0;
	}

	public String getHeuristicName() {
		return "Unweighted Normalized Dispersion Based";
	}

}