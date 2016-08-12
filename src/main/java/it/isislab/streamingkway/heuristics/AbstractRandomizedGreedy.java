package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
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
		
		DistributedRandomNumberGenerator prob = new DistributedRandomNumberGenerator(k);
		
		Map<Integer,Collection<Node>> partitions = partitionMap.getPartitions();
		Map<Integer, Double> probs = new ConcurrentHashMap<Integer, Double>(k);
		
		//populate first probs
		Stream<Entry<Integer,Collection<Node>>> parallelStream = partitions.entrySet().parallelStream();
		parallelStream
			.filter(p -> p.getValue().size() <= c)
			.forEach(
				new Consumer<Entry<Integer, Collection<Node>>>() {
					public void accept(Entry<Integer, Collection<Node>> t) {
						int index = t.getKey();
						double intersectNumber = (double) partitionMap.getIntersectionValueParallel(n, index);
						double partitionSize = t.getValue().size();
						probs.put(index, intersectNumber * getWeight(partitionSize, c));
					}
				}
		);
		
		if (probs.isEmpty()) {
			return new BalancedHeuristic().getIndex(g, partitionMap, n);
		}
		
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
		} else if (Z == 0) {
			return new BalancedHeuristic().getIndex(g, partitionMap, n);
		}
		
		//populate 
		prob.setDistribution(probs, Z);
		
		int index = -1;
		do {
			index = prob.getDistributedRandomNumber();
			if (partitions.containsKey(index) && partitions.get(index).size() > c) {
				prob.removeNumber(index);
				continue;
			}
		} while(!prob.isEmpty() && partitionMap.getPartitionSize(index) > c);
		
		return index == -1? new BalancedHeuristic().getIndex(g, partitionMap, n) : index;
	}

	public abstract Double getWeight(Double partitionSize, Integer c);
	public abstract String getHeuristicName();

}
