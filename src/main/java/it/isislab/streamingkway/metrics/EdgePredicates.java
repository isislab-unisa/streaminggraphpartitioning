package it.isislab.streamingkway.metrics;

import java.util.function.Predicate;

import org.graphstream.graph.Edge;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;


public class EdgePredicates {

	public static Predicate<Edge> isCuttingEdge() {
		return edg -> edg.getNode0().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) &&
				edg.getNode1().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) &&
				!edg.getNode0().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)
				.equals(edg.getNode1().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
	}
	
}
