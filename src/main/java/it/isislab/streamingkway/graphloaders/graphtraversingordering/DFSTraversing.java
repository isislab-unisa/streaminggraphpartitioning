package it.isislab.streamingkway.graphloaders.graphtraversingordering;

import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class DFSTraversing implements GraphTraversingOrdering {

	public Iterator<Node> getNodesOrdering(Graph g, Node v) {
		return v.getDepthFirstIterator();
	}

}
