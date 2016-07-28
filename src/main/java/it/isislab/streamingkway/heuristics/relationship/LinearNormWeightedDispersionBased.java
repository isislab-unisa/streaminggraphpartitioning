package it.isislab.streamingkway.heuristics.relationship;

public class LinearNormWeightedDispersionBased extends AbstractAbsDispersionBased {

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - intersectNumber / c;
	}

	public String getHeuristicName() {
		return "Linear Normalized Weighted Dispersion Based";
	}

}
