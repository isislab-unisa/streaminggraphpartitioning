package it.isislab.streamingkway.heuristics;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractDeterministicGreedy implements SGPHeuristic,WeightedHeuristic {


	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n)  {
		
		Map<Integer,Set<Node>> partitions = partitionMap.getPartitions();
		Integer index  = -1; 
		double max = Double.NEGATIVE_INFINITY;
		Integer c = partitionMap.getC();
		Integer k = partitionMap.getK();

		for (Entry<Integer, Set<Node>> partition : partitions.entrySet()) {
			Integer partitionSize = partition.getValue().size();
			Integer partitionIndx = partition.getKey();
			if (partitionSize >= c) {
				continue;
			}
			//computate score
			double intersectNumber = (double) partitionMap.getIntersectionValueParallel(n, partitionIndx);
			double weight = getWeight(intersectNumber, c);
			intersectNumber *= weight;

			if (Math.max(max, intersectNumber) == intersectNumber) {
				max = intersectNumber;
				index = partitionIndx;
			}
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
		//it cannot happens
		return index == -1 ? new Random().nextInt(k) + 1 : index;
	}

	public abstract Double getWeight(Double intersectNodes, Integer c);
	public abstract String getHeuristicName();

}
