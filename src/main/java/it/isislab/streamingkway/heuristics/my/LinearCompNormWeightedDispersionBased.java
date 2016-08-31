package it.isislab.streamingkway.heuristics.my;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;

public class LinearCompNormWeightedDispersionBased extends AbstractCompleteNormDispersionBased implements LinearWeightedHeuristic{

	public LinearCompNormWeightedDispersionBased() {super();}
	

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

	public String getHeuristicName() {
		return "Linear " + super.getHeuristicName();
	}

}
