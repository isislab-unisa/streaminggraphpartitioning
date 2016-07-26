package it.isislab.streamingkway.heuristics.my;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Consumer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class ParallelAbstractDeterministicGreedy implements SGPHeuristic,WeightedHeuristic {

	private Integer index ;
	private double max;

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n)  {
		index = -1;
		max = Double.NEGATIVE_INFINITY;
		Integer c = partitionMap.getC();
		Integer k = partitionMap.getK();
		Map<Integer, Collection<Node>> partitions = partitionMap.getPartitions();

		partitions.entrySet().parallelStream().forEach(new Consumer<Entry<Integer,Collection<Node>>>() {

			public void accept(Entry<Integer, Collection<Node>> partition) {
				Integer partitionSize = partition.getValue().size();
				Integer partitionIndx = partition.getKey();
				if (partitionSize >= c) {
					return;
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
			
		});
		return index == -1 ? new Random().nextInt(k) + 1 : index;
	}

	public abstract Double getWeight(Double intersectNodes, Integer c);
	public abstract String getHeuristicName();

}
