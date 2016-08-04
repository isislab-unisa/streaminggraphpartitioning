package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;


public class BalancedHeuristic implements SGPHeuristic {

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {		
		Map<Integer, Collection<Node>> partitions = partitionMap.getPartitions();

		Integer minimumSizeIndex = partitions.entrySet().parallelStream()
				.min(new Comparator<Entry<Integer,Collection<Node>>>() {
					public int compare(Entry<Integer, Collection<Node>> p1,Entry<Integer, Collection<Node>> p2) {
						Integer part1size = p1.getValue().size();
						Integer part2size = p2.getValue().size();
						Integer sizeDiff = part1size - part2size;
						if (sizeDiff == 0) {
							return ThreadLocalRandom.current().nextInt(-1, 2);
						} else {
							return sizeDiff;
						}
					}
				}).get().getKey();
		
		return minimumSizeIndex;
	}


	public String getHeuristicName() {
		return "Balanced";
	}
	
	
	
}
