package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;
import it.isislab.streamingkway.utils.DistributedRandomNumberGenerator;

public abstract class AbstractRandomizedGreedy implements SGPHeuristic,WeightedHeuristic {
	

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		double Z;
		int c = partitionMap.getC();
		int k = partitionMap.getK();
		
		DistributedRandomNumberGenerator prob = new DistributedRandomNumberGenerator();
		
		Map<Integer,Collection<Node>> partitions = partitionMap.getPartitions();
		Map<Integer, Double> probs = new ConcurrentHashMap<Integer, Double>(k);
		
		//populate first probs
		Stream<Entry<Integer,Collection<Node>>> parallelStream = partitions.entrySet().parallelStream();
		parallelStream.forEach(
				new Consumer<Entry<Integer, Collection<Node>>>() {
					public void accept(Entry<Integer, Collection<Node>> t) {
						int index = t.getKey();
						double intersectNumber = (double) partitionMap.getIntersectionValueParallel(n, index);						
						probs.put(index, intersectNumber * getWeight(intersectNumber, c));
					}
				}
		);
		
		//calculate Z
		Z = probs.values().parallelStream()
				.mapToDouble( p -> p.doubleValue())
				.sum();
		if (Z > 0) {
			probs.entrySet().parallelStream().forEach(new Consumer<Entry<Integer, Double>>() {
				public void accept(Entry<Integer,Double> t) {
					probs.put(t.getKey(), t.getValue() / Z);
				}
			});			
		}
		
		//populate 
		prob.setDistribution(probs);
		
		
		int index = -1;
		do {
			index = prob.getDistributedRandomNumber();
			if (partitions.get(index).size() >= c) {
				prob.removeNumber(index);
			}
		} while(partitions.get(index).size() >= c);
		
		return index == -1? new Random().nextInt(k) + 1 : index;
	}

	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
