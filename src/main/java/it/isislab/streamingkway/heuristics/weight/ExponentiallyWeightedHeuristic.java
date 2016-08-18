package it.isislab.streamingkway.heuristics.weight;

public interface ExponentiallyWeightedHeuristic extends WeightedHeuristic {

	default Double getWeightEx(Double partitionSize, Integer c) {
		return 1 - Math.exp(partitionSize - c);
	}
	
}
