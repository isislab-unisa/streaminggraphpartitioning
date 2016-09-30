package it.isislab.streamingkway.heuristics.relationship.newh;

import it.isislab.streamingkway.heuristics.relationship.AbstractSTCHeuristic;

public class ExponentiallySTCHeuristic extends AbstractSTCHeuristic {

	public ExponentiallySTCHeuristic(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0 - Math.exp(partitionSize - c);
	}
	public String getHeuristicName() {
		return "Exponentially" + super.getHeuristicName();
	}	
}
