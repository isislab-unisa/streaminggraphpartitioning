package it.isislab.streamingkway.graphloaders.graphtraversingordering;

import java.util.Iterator;
import java.util.stream.Stream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class RandomTraversing implements GraphTraversingOrdering {

	RandomComparator<Node> rc = new RandomComparator<Node>();
	
	public Iterator<Node> getNodesOrdering(Graph g, Node v) {
		Stream<Node> nodes = g.getNodeSet().stream().sorted(rc);
		return nodes.iterator();
	}

}
