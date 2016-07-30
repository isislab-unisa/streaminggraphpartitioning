package it.isislab.streamingkway.heuristics.relationship.distance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class Dispersion {

	public static List<Node> cuvCalculator(Node u, Node v) {
		Collection<Edge> minDegreeEdges = null;
		List<Node> cuv = null;
		Node maxDegNode = null;
		Node minDegNode = null;
		int vDeg;
		int uDeg;
		if ((vDeg = v.getDegree()) < (uDeg= u.getDegree())) {
			cuv = new ArrayList<>(vDeg);
			minDegreeEdges = v.getEdgeSet();
			minDegNode = v;
			maxDegNode = u;
		} else {
			cuv = new ArrayList<>(uDeg);
			minDegreeEdges = u.getEdgeSet();
			minDegNode = u;
			maxDegNode = v;
		}
		

		for (Edge cuvEdge : minDegreeEdges) {
			Node z = cuvEdge.getOpposite(minDegNode);
			if (z.hasEdgeBetween(maxDegNode)) {
				cuv.add(z);
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
