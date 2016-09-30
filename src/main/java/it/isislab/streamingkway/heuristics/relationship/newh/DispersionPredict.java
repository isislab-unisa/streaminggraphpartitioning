package it.isislab.streamingkway.heuristics.relationship.newh;

import it.isislab.streamingkway.heuristics.relationship.AbstractDispersionPredict;

public class DispersionPredict extends AbstractDispersionPredict {

	public DispersionPredict(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0;
	}
	public String getHeuristicName() {
		return "Unweighted" + super.getHeuristicName();
	}	
}
