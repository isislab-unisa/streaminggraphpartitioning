package it.isislab.streamingkway.metrics;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import it.isislab.streamingkway.partitions.PartitionMap;

public class ParallelQualityChecker implements QualityChecker {

	public Integer getCuttingEdgesCount(Graph gr) {
		Integer cuttingEdges = 0;
		Collection<Edge> edges = gr.getEdgeSet();
		Stream<Edge> parallelEdges = edges.parallelStream();
		cuttingEdges = (int) parallelEdges.filter(EdgePredicates.isCuttingEdge()).count();

		return cuttingEdges;
	}

	public Double getCuttingEdgeRatio(Graph gr) {
		return (double)this.getCuttingEdgesCount(gr)/gr.getEdgeCount();
	}
	
	public Double getDisplacement(PartitionMap pm) {
		Map<Integer,Integer> partitionsSize = pm.getPartitionsSize();
		Integer c = pm.getC();
		//TODO to doubleize
		return (double) partitionsSize.values().parallelStream().mapToInt(
				p -> p.intValue() <= c ? c - p.intValue() : p.intValue() -c
				).sum();
	}

	public Double getNormalizedMaximumLoad(PartitionMap pm, Graph gr) {
		Map<Integer,Integer> partitionsSize = pm.getPartitionsSize();
		int max = partitionsSize.values().parallelStream().mapToInt(p -> p.intValue()).max().getAsInt();
		int nk = gr.getNodeCount()/pm.getK();
		return (double)max/nk;
	}


}
