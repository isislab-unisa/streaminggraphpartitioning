package it.isislab.streamingkway.heuristics.relationship;

public class ExponentiallyWeightedDispersionBased extends AbstractDispersionBased {

	public String getHeuristicName() {
		return "Exponentially Weighted Dispersion Based";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - Math.exp(intersectNumber - c);
	}

}
