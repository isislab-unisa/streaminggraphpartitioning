package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractRandomizedGreedy implements SGPHeuristic,WeightedHeuristic {
	
	protected boolean parallel;
	
	public AbstractRandomizedGreedy(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		double Z;
		int c = partitionMap.getC();
		int k = partitionMap.getK();
		
		
		Map<Integer,Collection<Node>> partitions = partitionMap.getPartitions();
		Map<Integer, Double> probs = new ConcurrentHashMap<Integer, Double>(k);
		
		//populate first probs
		Stream<Entry<Integer,Collection<Node>>> str = partitions.entrySet().stream();
		if (parallel) {
			str = str.parallel();
		}
		str
			.filter(p -> p.getValue().size() <= c)
			.forEach(
				new Consumer<Entry<Integer, Collection<Node>>>() {
					public void accept(Entry<Integer, Collection<Node>> t) {
						int index = t.getKey();
						double intersectNumber = (double) partitionMap.getIntersectionValue(n, index);
						double partitionSize = t.getValue().size();
						probs.put(index, intersectNumber * getWeight(partitionSize, c));
					}
				}
		);
		
		if (probs.isEmpty()) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		
		//calculate Z
		Stream<Double> zStr = probs.values().stream();
		if (parallel) {
			zStr = zStr.parallel();
		}
		Z = zStr
				.mapToDouble( p -> p.doubleValue())
				.sum();
		if (Z > 0) {
			Stream<Entry<Integer, Double>> scoreStream = probs.entrySet().stream();
			if (parallel) {
				scoreStream = scoreStream.parallel();
			}
			scoreStream
				.forEach(
					new Consumer<Entry<Integer, Double>>() {
						public void accept(Entry<Integer,Double> t) {
							probs.put(t.getKey(), t.getValue() / Z);
						}
					}
		);			
		} else if (Z == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		
		//populate 
		int index = -1;
		double randomN = Math.random();
		double cumProbability = 0.0;
		for (Entry<Integer, Double> part: probs.entrySet()) {
			cumProbability += part.getValue();
			if (randomN <= cumProbability && part.getValue() != 0) {
				index = part.getKey();
			}
		}
		
		return index == -1? new BalancedHeuristic(parallel).getIndex(partitionMap, n) : index;
	}

	public abstract Double getWeight(Double partitionSize, Integer c);
	public String getHeuristicName() {
		return "Randomized Greedy";
	}

}
