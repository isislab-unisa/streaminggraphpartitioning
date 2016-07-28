package it.isislab.streamingkway.heuristics.relationship;

public class ExponentiallyAbsWeightedDispersionBased extends AbstractAbsDispersionBased {

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - Math.exp(intersectNumber - c);
	}

	public String getHeuristicName() {
		return "Exponentially Weighted Dispersion Based";
	}

}
