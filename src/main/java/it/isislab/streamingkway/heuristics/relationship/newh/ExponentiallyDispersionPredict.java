package it.isislab.streamingkway.heuristics.relationship.newh;

import it.isislab.streamingkway.heuristics.relationship.AbstractDispersionPredict;

public class ExponentiallyDispersionPredict extends AbstractDispersionPredict {

	public ExponentiallyDispersionPredict(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1 - Math.exp(partitionSize - c);
	}
	public String getHeuristicName() {
		return "Exponentially" + super.getHeuristicName();
	}	
}
