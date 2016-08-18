package it.isislab.streamingkway.heuristics.weight;

public interface WeightedHeuristic {

	public Double getWeight(Double partitionSize, Integer c);
	
}
