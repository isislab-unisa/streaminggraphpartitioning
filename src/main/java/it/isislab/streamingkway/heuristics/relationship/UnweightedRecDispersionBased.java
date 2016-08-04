package it.isislab.streamingkway.heuristics.relationship;

public class UnweightedRecDispersionBased extends AbstractRecursiveDispersionBased {

	public String getHeuristicName() {
		return "Unweighted Recursive Dispersion Based";
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0;
	}

}
