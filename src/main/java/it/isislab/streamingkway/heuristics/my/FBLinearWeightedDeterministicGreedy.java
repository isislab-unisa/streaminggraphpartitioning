package it.isislab.streamingkway.heuristics.my;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class FBLinearWeightedDeterministicGreedy implements SGPHeuristic {


	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer index = -1;
		Integer c = partitionMap.getC();
		Integer k = partitionMap.getK();
		Map<Integer, Set<Node>> partitions = partitionMap.getPartitions();
		double max = Double.NEGATIVE_INFINITY;
		for (Entry<Integer, Set<Node>> partition : partitions.entrySet()) {
			Integer partitionSize = partition.getValue().size();
			Integer partitionIndx = partition.getKey();
			if (partitionSize >= c) {
				continue;
			}
			double intersectNumber = 1+(double) partitionMap.getIntersectionValue(n, partitionIndx);
			double weight = 1 - ((double)partitionSize/(double)c);
			intersectNumber *= weight;
			if (Math.max(max, intersectNumber) == intersectNumber) {
				max = intersectNumber;
				index = partitionIndx;
			}
			//TODO tiebreak
			else if (Double.compare(max, intersectNumber) == 0) { //tie break
				int competitorPartitionSize = partitions.get(index).size();
				if (competitorPartitionSize > partitionSize) { //old partition are greater
					max = intersectNumber; //i win!
					index = partitionIndx;
				} else if (competitorPartitionSize == partitionSize) {
					boolean ivsc = new Random().nextBoolean();
					if (ivsc) {
						max = intersectNumber;
						index = partitionIndx;
					}
				} 
			}
		}

		return index == -1 ? new Random().nextInt(k) + 1 : index;
	}

	public String getHeuristicName() {
		return "Forced Balanced Linear Deterministic Greedy Euristic";
	}

}
