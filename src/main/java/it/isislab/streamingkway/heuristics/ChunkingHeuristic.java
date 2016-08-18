package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public class ChunkingHeuristic implements SGPHeuristic {

	protected int t = 1;
	
	public Integer getIndex(PartitionMap partitionMap, Node n)  {
		Integer index = -1;
		Integer c = partitionMap.getC();
		index = (int) (Math.ceil((double)t/c));
		t++;
		return index;
	}

	public String getHeuristicName() {
		return "Chunking";
	}
	
}
