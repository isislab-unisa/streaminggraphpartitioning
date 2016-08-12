package it.isislab.streamingkway.heuristics.relationship;

public class UnweightedNormDispersionBased extends AbstractNormDispersionBased {

	public UnweightedNormDispersionBased() {super();}
	
	public UnweightedNormDispersionBased(Double A, Double B, Double C) {
		super(A, B, C);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0;
	}

	public String getHeuristicName() {
		return "Unweighted Normalized Dispersion Based";
	}

}
