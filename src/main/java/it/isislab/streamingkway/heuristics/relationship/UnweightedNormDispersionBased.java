package it.isislab.streamingkway.heuristics.relationship;

public class UnweightedNormDispersionBased extends AbstractNormDispersionBased {

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0;
	}

	public String getHeuristicName() {
		return "Unweighted Normalized Dispersion Based";
	}

}
