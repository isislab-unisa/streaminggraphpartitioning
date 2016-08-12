package it.isislab.streamingkway.heuristics.relationship;

public class LinearNormWeightedDispersionBased extends AbstractNormDispersionBased{

	public LinearNormWeightedDispersionBased() {super();}
	
	public LinearNormWeightedDispersionBased(Double A, Double B, Double C) {
		super(A, B, C);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - partitionSize / c;
	}

	public String getHeuristicName() {
		return "Linear Normalized Weighted Dispersion Based";
	}

}
