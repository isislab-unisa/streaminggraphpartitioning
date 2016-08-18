package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class HashingHeuristic implements SGPHeuristic {

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer vIndex = Integer.parseInt(n.getId());
		Integer K = partitionMap.getK();
		Integer index = (vIndex % K ) + 1;
		
		return index;
	}

	public String getHeuristicName() {
		return "Hashing";
	}

}
