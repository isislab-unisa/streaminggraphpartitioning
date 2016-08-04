package it.isislab.streamingkway.heuristics.relationship;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.LinearWeightedDeterministicGreedy;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.WeightedHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractAbsDispersionBased  implements SGPHeuristic, WeightedHeuristic{

	public DistanceFunction dist = new SimpleDistanceFunction();

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();
		
		if (n.getDegree() == 0) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
		
		Map<Node, Integer> nodeScores = new HashMap<Node, Integer>(n.getDegree());
		
		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();

		nNeighIt.forEachRemaining(p -> nodeScores.put(p, Dispersion.getDispersion(p, n, dist)));
		
		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		nodeScores.entrySet().parallelStream().forEach(new Consumer<Entry<Node,Integer>>() {
			public void accept(Entry<Node, Integer> t) {
				Node v = t.getKey();
				if (!v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
					return;
				}
				Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
				if (partitionIndex <= 0 || partitionIndex > partitionMap.getK()) {
					return;
				}
				Integer partitionSize = partitionMap.getPartitionSize(partitionIndex);
				if (partitionSize > c) {
					return;
				}
				if (partitionsScores.containsKey(partitionIndex)){
					partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex)) + t.getValue() 
					* getWeight((double)partitionSize, c));
				} else {
					partitionsScores.put(partitionIndex, (double) t.getValue() *
							getWeight((double) partitionSize, c));
				}
			}
			
		});
		
		if (partitionsScores.isEmpty()) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
		Integer maxPart = partitionsScores.entrySet().parallelStream()
				.max(new Comparator<Entry<Integer, Double>>() {
					public int compare(Entry<Integer, Double> e1, Entry<Integer, Double> e2) {
						Integer size1 = partitionMap.getPartitionSize(e1.getKey());
						Integer size2 = partitionMap.getPartitionSize(e2.getKey());
						Double score1 = e1.getValue();
						Double score2 = e2.getValue();
						if (Double.max(score1, score2) == score1) {
							return 1;
						} else if (Double.max(score1, score2) == score2) {
							return -1;
						} else { //tie break
							return size1 - size2;
						}
					}
				}).get().getKey();

		return maxPart;
		
	}
	


	public abstract Double getWeight(Double partitionSize, Integer c);
	public abstract String getHeuristicName();

}
