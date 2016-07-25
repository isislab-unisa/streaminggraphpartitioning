package it.isislab.streamingkway.graphloaders.graphtraversingordering;

import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public interface GraphTraversingOrdering {

	public Iterator<Node> getNodesOrdering(Graph g, Node v);
	
}
