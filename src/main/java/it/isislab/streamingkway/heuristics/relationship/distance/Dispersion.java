package it.isislab.streamingkway.heuristics.relationship.distance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Node;

public class Dispersion {

	public static List<Node> cuvCalculator(Node u, Node v) {

		List<Node> cuv = new ArrayList<>(u.getDegree());
		Iterator<Node> uIterator = u.getNeighborNodeIterator();

		while (uIterator.hasNext()) {
			Node w = uIterator.next();
			if (v.hasEdgeBetween(w)) {
				cuv.add(w);
			}
		}
		
		return cuv;
	}
	
	public static Integer getDispersion(Node u, Node v, DistanceFunction dist) {
		Integer dispValue = 0;
		List<Node> cuv = cuvCalculator(u, v);
		int l = cuv.size();
		for (int i = 0; i < l; i++) {
			for (int j = i+1; j < l; j++) {
				dispValue += dist.getDistance(cuv.get(i), cuv.get(j), u, v);
			}
		}
		return dispValue;
	}

	public static Integer getDispersion(Node u, Node v, DistanceFunction dist, List<Node> cuv) {
		Integer dispValue = 0;
		int l = cuv.size();
		for (int i = 0; i < l; i++) {
			for (int j = i+1; j < l; j++) {
				dispValue += dist.getDistance(cuv.get(i), cuv.get(j), u, v);
			}
		}
		return dispValue;
	}

	
}
