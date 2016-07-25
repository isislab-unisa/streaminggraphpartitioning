package it.isislab.streamingkway.heuristics;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

/**
 * @author dar
 *
 */
public class BalancedHeuristic implements SGPHeuristic {
	

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {		
		Integer index = -1;
		Map<Integer, Set<Node>> partitions = partitionMap.getPartitions();
		Integer min = Integer.MAX_VALUE;
		for (Entry<Integer, Set<Node>> partition : partitions.entrySet()) {
			int size = partition.getValue().size();
			if (size < min) {
				min = size;
				index = partition.getKey();
			}
		}
		
		return index;
	}


	public String getHeuristicName() {
		return "Balanced";
	}
	
	
	
}
