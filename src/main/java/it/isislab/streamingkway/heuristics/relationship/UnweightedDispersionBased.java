package it.isislab.streamingkway.heuristics.relationship;

public class UnweightedDispersionBased extends AbstractDispersionBased {

	public String getHeuristicName() {
		return "Unweighted Dispersion Based";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1.0;
	}

}
