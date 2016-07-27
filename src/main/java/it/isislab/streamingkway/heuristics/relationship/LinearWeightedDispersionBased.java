package it.isislab.streamingkway.heuristics.relationship;

public class LinearWeightedDispersionBased extends AbstractDispersionBased {
	
	public String getHeuristicName() {
		return "Linear Weighted Dispersion Based";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - intersectNumber/c;
	}

}
