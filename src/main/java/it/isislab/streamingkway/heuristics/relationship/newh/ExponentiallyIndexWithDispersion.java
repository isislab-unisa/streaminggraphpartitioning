package it.isislab.streamingkway.heuristics.relationship.newh;

import it.isislab.streamingkway.heuristics.relationship.AbstractIndexWithDispersion;

public class ExponentiallyIndexWithDispersion extends AbstractIndexWithDispersion {

	public ExponentiallyIndexWithDispersion(boolean parallel) {
		super(parallel);
	}


	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0 - Math.exp(partitionSize - c);
	}

	public String getHeuristicName() {
		return "Exponentially" + super.getHeuristicName();
	}	
}
