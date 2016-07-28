package it.isislab.streamingkway.heuristics.relationship;

public class LinearRecWeightedDispersionBased extends AbstractRecursiveDispersionBased {
	
	public String getHeuristicName() {
		return "Linear Recursive Dispersion Based";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - intersectNumber/c;
	}

}
