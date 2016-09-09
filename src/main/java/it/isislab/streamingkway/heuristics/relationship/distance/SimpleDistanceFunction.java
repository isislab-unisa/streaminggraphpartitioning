package it.isislab.streamingkway.heuristics.relationship.distance;

import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class SimpleDistanceFunction implements DistanceFunction {

	private final static Integer DIST_POS = 1;
	private final static Integer DIST_NEG = 1 - DIST_POS;
	/**
	 * Returns {@value DIST_POST} if s has not an edge between t of if s and t have no common neighbors.
	 * Returns {@value DIST_NEG} otherwise
	 */
	public Integer getDistance(Node s, Node t, Node u, Node v) {
		if (!s.hasEdgeBetween(t)) {
			return DIST_POS;
		}

		Iterator<Edge> sEdgesIt = s.getEdgeIterator();
		Iterator<Edge> tEdgesIt = t.getEdgeIterator();
		while (sEdgesIt.hasNext()) {
			Node w = sEdgesIt.next().getOpposite(s);
			while (tEdgesIt.hasNext()) {
				Node z = tEdgesIt.next().getOpposite(t);
				
				if (u.equals(z) || u.equals(w) || v.equals(z) || v.equals(w)) {
					continue;
				}
				if (w.hasEdgeBetween(z)) {
					return DIST_POS;
				}
			}
		}
		
		
		
		return DIST_NEG;
	}

}
