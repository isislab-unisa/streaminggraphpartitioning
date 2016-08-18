package it.isislab.streamingkway.heuristics.weight;

public interface LinearWeightedHeuristic extends WeightedHeuristic {

	default Double getWeightLin(Double partitionSize, Integer c) {
		return 1 - partitionSize/c;
	}
}
