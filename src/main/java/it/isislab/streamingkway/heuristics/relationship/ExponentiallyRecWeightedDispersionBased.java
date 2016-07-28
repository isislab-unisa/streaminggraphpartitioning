package it.isislab.streamingkway.heuristics.relationship;

public class ExponentiallyRecWeightedDispersionBased extends AbstractRecursiveDispersionBased {

	public String getHeuristicName() {
		return "Exponentially Recursive Dispersion Based";
	}

	public Double getWeight(Double intersectNumber, Integer c) {
		return 1 - Math.exp(intersectNumber - c);
	}

}
