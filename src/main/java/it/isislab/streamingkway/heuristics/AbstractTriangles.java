package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Comparator;
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
						double tri1 = partitionMap.getTrianglesValue(n, p1.getKey()) * w1;
						double tri2 = partitionMap.getTrianglesValue(n, p2.getKey()) * w2;
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


	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
