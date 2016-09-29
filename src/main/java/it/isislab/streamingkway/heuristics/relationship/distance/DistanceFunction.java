package it.isislab.streamingkway.heuristics.relationship.distance;

import org.graphstream.graph.Node;

public interface DistanceFunction {
	
	public final static Integer DIST_POS = 1;
	public final static Integer DIST_NEG = 1 - DIST_POS;
	
	public Integer getDistance(Node s, Node t, Node u, Node v);
	public Integer getDistance(Node s, Node t, Node u, Node v,int emb);
	
}
