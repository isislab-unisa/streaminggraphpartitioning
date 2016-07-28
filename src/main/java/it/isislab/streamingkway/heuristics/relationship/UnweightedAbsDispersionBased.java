package it.isislab.streamingkway.heuristics.relationship;

public class UnweightedAbsDispersionBased extends AbstractAbsDispersionBased {

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1.0;
	}

	public String getHeuristicName() {
		return "Unweighted Absolute Dispersion Based";
	}

}
