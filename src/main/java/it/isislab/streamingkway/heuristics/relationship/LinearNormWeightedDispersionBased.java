package it.isislab.streamingkway.heuristics.relationship;

public class LinearNormWeightedDispersionBased extends AbstractNormDispersionBased{

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - partitionSize / c;
	}

	public String getHeuristicName() {
		return "Linear Normalized Weighted Dispersion Based";
	}

}
