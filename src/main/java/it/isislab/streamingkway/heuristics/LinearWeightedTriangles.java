package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.LinearWeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class LinearWeightedTriangles extends AbstractTriangles implements LinearWeightedHeuristic{

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		return super.getIndex(partitionMap, n);
	}	
	
	public String getHeuristicName() {
		return "Linear Weighted Triangles";
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return getWeightLin(partitionSize, c);
	}

}
