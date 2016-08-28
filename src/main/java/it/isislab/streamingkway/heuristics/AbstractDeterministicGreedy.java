package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractDeterministicGreedy implements SGPHeuristic, WeightedHeuristic {

	protected boolean parallel;
	
	public AbstractDeterministicGreedy(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n)  {
		
		Map<Integer,Collection<Node>> partitions = partitionMap.getPartitions();
		Integer c = partitionMap.getC();

		Stream<Entry<Integer, Collection<Node>>> str = partitions.entrySet().stream();
		
		if (parallel) {
			str = str.parallel();
		}
		
		Integer maxIndex = str
				.filter(p -> p.getValue().size() <= c)
				.max(new Comparator<Map.Entry<Integer,Collection<Node>>>() {
					public int compare(Map.Entry<Integer,Collection<Node>> p1,
							Map.Entry<Integer,Collection<Node>> p2) {
						int p1size = p1.getValue().size();
						int p2size = p2.getValue().size();
					
						double intersect1 = partitionMap.getIntersectionValueParallel(n, p1.getKey());
						double intersect2 = partitionMap.getIntersectionValueParallel(n, p2.getKey());
						double w1 = getWeight((double)p1size, c);
						double w2 = getWeight((double)p2size, c);
						intersect1 *= w1;
						intersect2 *= w2;
						if (Double.max(intersect1, intersect2) == intersect1) {
							return 1;
						} else if (Double.max(intersect1, intersect2) == intersect2) {
							return -1;
						} else { //tie break
							return p1size - p2size;
						}
					}
				}).get().getKey();
		
		return maxIndex;
	}

	public abstract Double getWeight(Double partitionSize, Integer c);
	public abstract String getHeuristicName();

}
