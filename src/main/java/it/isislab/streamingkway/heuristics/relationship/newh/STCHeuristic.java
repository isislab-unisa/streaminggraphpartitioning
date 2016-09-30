package it.isislab.streamingkway.heuristics.relationship.newh;

import it.isislab.streamingkway.heuristics.relationship.AbstractSTCHeuristic;

public class STCHeuristic extends AbstractSTCHeuristic {

	public STCHeuristic(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0;
	}
	public String getHeuristicName() {
		return "Linear" + super.getHeuristicName();
	}	
}
