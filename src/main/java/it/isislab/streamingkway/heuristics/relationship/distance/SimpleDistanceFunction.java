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
		if (s.hasEdgeBetween(t)) {
			return DIST_NEG;
		}

		Iterator<Edge> sEdgesIt = s.getEdgeIterator();
		while (sEdgesIt.hasNext()) {
			Iterator<Edge> tEdgesIt = t.getEdgeIterator();
			Edge sw = sEdgesIt.next();
			if (sw.getOpposite(s).equals(u) || sw.getOpposite(s).equals(v)) {
				continue;
			}
			while (tEdgesIt.hasNext()) {
				Node z = tEdgesIt.next().getOpposite(t);
				
				if (z.equals(sw.getOpposite(s))) {
					return DIST_NEG;
				}
				
			}
		}
		
		
		
		return DIST_POS;
	}

}
