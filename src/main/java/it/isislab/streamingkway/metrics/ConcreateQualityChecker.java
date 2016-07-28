package it.isislab.streamingkway.metrics;

import java.util.Collection;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.partitions.PartitionMap;
import scala.NotImplementedError;

public class ConcreateQualityChecker implements QualityChecker {

	public Integer getCuttingEdgesCount(Graph gr) {
		Integer cuttingEdges = 0;
		Collection<Edge> edges = gr.getEdgeSet();
		
		for (Edge edge : edges) {
			Node n0 = edge.getNode0();
			Node n1 = edge.getNode1();
			if (n0.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) &&
					n1.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
				
				if (!n0.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE).equals(
						n1.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) {
					cuttingEdges++;
				}
				
			}
		}

		return cuttingEdges;
	}

	public Double getDisplacement(PartitionMap pm) {
		//TODO
		throw new NotImplementedError("Not implemented yet");
	}

	public Double getCuttingEdgeRatio(Graph gr) {
		return (double)getCuttingEdgesCount(gr)/gr.getNodeCount();
	}

	@Override
	public Double getNormalizedMaximumLoad(PartitionMap pm, Graph gr) {
		// TODO Auto-generated method stub
		throw new NotImplementedError("Not implemented yet");
	}

}
