package it.isislab.streamingkway.heuristics.relationship.newh;

import it.isislab.streamingkway.heuristics.relationship.AbstractDispersionForKway;

public class LinearDispersionForKway extends AbstractDispersionForKway {

	public LinearDispersionForKway(boolean parallel) {
		super(parallel);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0 - partitionSize/c;
	}
	
	public String getHeuristicName() {
		return "Linear" + super.getHeuristicName();
	}	

}
