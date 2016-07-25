package it.isislab.streamingkway.graphloaders.factory;

import it.isislab.streamingkway.graphloaders.graphtraversingordering.BFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.DFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.RandomTraversing;

public class OrderingFactory {

	public static GraphTraversingOrdering getOrdering(String t) {
		switch (t) {
		case "R": ;
		case Ordering.RANDOM_ORDER:
			return new RandomTraversing();
		case "B": ;
		case Ordering.BFS_ORDER:
			return new BFSTraversing();
		case "D": ;
		case Ordering.DFS_ORDER:
			return new DFSTraversing();
		case "S": ;
		default:
			return null;
		}
	}

}
