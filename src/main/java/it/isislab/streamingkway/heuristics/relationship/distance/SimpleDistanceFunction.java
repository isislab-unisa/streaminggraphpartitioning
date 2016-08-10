package it.isislab.streamingkway.heuristics.relationship.distance;

import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class SimpleDistanceFunction implements DistanceFunction {

	public Integer getDistance(Node s, Node t, Node u, Node v) {
		if (!s.hasEdgeBetween(t)) {
			return 1;
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
					return 1;
				}
			}
		}
		
		
		
		return 0;
	}

}
