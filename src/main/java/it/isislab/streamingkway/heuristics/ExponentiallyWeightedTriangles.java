package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.ExponentiallyWeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class ExponentiallyWeightedTriangles extends AbstractTriangles implements ExponentiallyWeightedHeuristic {

	public ExponentiallyWeightedTriangles(boolean parallel) {
		super(parallel);
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		return super.getIndex(partitionMap, n);
	}

	public String getHeuristicName() {
		return "Exponentially Weighted Triangles"+ (parallel ? " Parallel" : "");
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightEx(partitionSize, c);
	}

}
