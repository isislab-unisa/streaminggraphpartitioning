package it.isislab.streamingkway.heuristics.relationship.distance;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class Dispersion {

	public static List<Node> cuvCalculator(Node u, Node v) {
		List<Node> cuv = new ArrayList<>(Math.min(v.getDegree(), u.getDegree()));
		for (Edge cuvEdge : v.getEdgeSet()) {
			Node z = cuvEdge.getOpposite(v); //z is a neighbour of v
			//check if z if already partitioned and has edge between u 
			if (z.hasEdgeBetween(u)) {
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
	
}
