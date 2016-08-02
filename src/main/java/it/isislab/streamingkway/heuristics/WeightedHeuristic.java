package it.isislab.streamingkway.heuristics;

public interface WeightedHeuristic {

	public Double getWeight(Double partitionSize, Integer c);
	
}
