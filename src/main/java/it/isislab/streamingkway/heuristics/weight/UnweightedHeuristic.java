package it.isislab.streamingkway.heuristics.weight;

public interface UnweightedHeuristic extends WeightedHeuristic {

	default Double getWeightUn(Double partitionSize, Integer c) {
		return 1.0;
	}
	
}
