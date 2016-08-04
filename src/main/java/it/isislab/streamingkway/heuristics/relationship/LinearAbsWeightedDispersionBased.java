package it.isislab.streamingkway.heuristics.relationship;

public class LinearAbsWeightedDispersionBased extends AbstractAbsDispersionBased {

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - partitionSize/c;
	}

	public String getHeuristicName() {
		return "Linear Weighted Absolute Dispersion Based";
	}

}
