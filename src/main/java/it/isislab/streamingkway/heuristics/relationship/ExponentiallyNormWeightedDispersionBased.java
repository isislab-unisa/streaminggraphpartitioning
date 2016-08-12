package it.isislab.streamingkway.heuristics.relationship;

public class ExponentiallyNormWeightedDispersionBased extends AbstractNormDispersionBased {

	public ExponentiallyNormWeightedDispersionBased() {super();}
	
	public ExponentiallyNormWeightedDispersionBased(Double A, Double B, Double C) {
		super(A, B, C);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - Math.exp(partitionSize - c);
	}

	public String getHeuristicName() {
		return "Exponentially Normalized Weighted Dispersion Based";
	}

}
