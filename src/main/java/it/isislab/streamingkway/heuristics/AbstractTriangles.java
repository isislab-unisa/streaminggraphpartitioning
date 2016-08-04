package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractTriangles implements SGPHeuristic,WeightedHeuristic {

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();
		Map<Integer,Collection<Node>> parts = partitionMap.getPartitions();

		Integer maxIndex = parts.entrySet().parallelStream()
				.max(new Comparator<Entry<Integer,Collection<Node>>>() {

					public int compare(Entry<Integer, Collection<Node>> p1,
							Entry<Integer, Collection<Node>> p2) {
						int p1size = partitionMap.getPartitionSize(p1.getKey());
						int p2size = partitionMap.getPartitionSize(p2.getKey());
						if (p1size > c) {
							return -1;
						}
						if (p2size > c) {
							return 1;
						}
						double w1 = getWeight((double)p1size, c);
						double w2 = getWeight((double)p2size, c);
						double tri1 = getTrianglesValue(n, partitionMap, p1.getKey()) * w1;
						double tri2 = getTrianglesValue(n, partitionMap, p2.getKey()) * w2;
						if (Math.max(tri1, tri2) == tri1) {
							return 1;
						} else if (Math.max(tri1, tri2) == tri2) {
							return 2;
						} else {
							return p1size - p2size;
						}
					}
				}).get().getKey();

		return maxIndex == -1 ? new BalancedHeuristic().getIndex(g, partitionMap, n) : maxIndex;
	}

	private double getTrianglesValue(Node n, PartitionMap parts, Integer partitionIndex) {
		int totalEdges = 0;

		List<Node> gammaNIntersect = parts.getIntersectionNodesParallel(n, partitionIndex);
		for (int i = 0; i < gammaNIntersect.size(); i++) {
			for (int j = i+1; j < gammaNIntersect.size(); j++) {
				if (gammaNIntersect.get(i).hasEdgeBetween(gammaNIntersect.get(j))) {
					totalEdges ++;
				}
			}
		}
		//calculate score
		Integer N = gammaNIntersect.size();
		Integer binCoeff = N == 1 || N == 0 ? 0 : N*(N-1)/2;
		if (binCoeff == 0) {  //hardcoded 0.0 because totalEdges must be 0 too. check it out
			return 0.0;
		}
		return (double)totalEdges/binCoeff; //safe to calculate because binCoeff != 0
	}

	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
