package it.isislab.streamingkway.heuristics.relationship.distance;

import org.graphstream.graph.Node;

public interface DistanceFunction {

	public Integer getDistance(Node s, Node t, Node u, Node v);
	
}
