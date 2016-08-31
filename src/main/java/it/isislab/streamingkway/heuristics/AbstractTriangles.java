package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractTriangles implements SGPHeuristic,WeightedHeuristic {

	protected boolean parallel;
	public AbstractTriangles(boolean parallel) {
		this.parallel = parallel;
	}
	
	public Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();
		Map<Integer,Collection<Node>> parts = partitionMap.getPartitions();
		
		Stream<Entry<Integer, Collection<Node>>> str =  parts.entrySet().stream();
		if(parallel) {
			str = str.parallel();
		}

		Integer maxIndex = str
				.filter(p -> p.getValue().size() <= c)
				.max(new Comparator<Entry<Integer,Collection<Node>>>() {

					public int compare(Entry<Integer, Collection<Node>> p1,
							Entry<Integer, Collection<Node>> p2) {
						int p1size = partitionMap.getPartitionSize(p1.getKey());
						int p2size = partitionMap.getPartitionSize(p2.getKey());
						double w1 = getWeight((double)p1size, c);
						double w2 = getWeight((double)p2size, c);
						double tri1 = partitionMap.getTrianglesValue(n, p1.getKey()) * w1;
						double tri2 = partitionMap.getTrianglesValue(n, p2.getKey()) * w2;
						if (Math.max(tri1, tri2) == tri1) {
							return 1;
						} else if (Math.max(tri1, tri2) == tri2) {
							return -1;
						} else {
							return p1size - p2size;
						}
					}
				}).get().getKey();

		return maxIndex;
	}


	public abstract Double getWeight(Double intersectNumber, Integer c);
	public String getHeuristicName() {
		return "Triangles";
	}

}
