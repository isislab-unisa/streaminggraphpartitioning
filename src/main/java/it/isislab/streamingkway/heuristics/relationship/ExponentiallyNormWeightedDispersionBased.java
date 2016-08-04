package it.isislab.streamingkway.heuristics.relationship;

public class ExponentiallyNormWeightedDispersionBased extends AbstractAbsDispersionBased {

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - Math.exp(partitionSize - c);
	}

	public String getHeuristicName() {
		return "Exponentially Normalized Weighted Dispersion Based";
	}

}
