package it.isislab.streamingkway.heuristics.relationship;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.graphstream.graph.Node;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractAbsDispersionBased  implements SGPHeuristic, WeightedHeuristic{

	public DistanceFunction dist = new SimpleDistanceFunction();

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic().getIndex(partitionMap, n);
		}
		//score for each neighbour 
		Map<Node, Integer> nodeScores = new HashMap<Node, Integer>(n.getDegree());

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();

		//1 + disp(u,v) in order to mix DG with ADB
		nNeighIt.forEachRemaining(p -> nodeScores.put(p, 1+ Dispersion.getDispersion(p, n, dist)));
		
		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		nodeScores.entrySet().parallelStream()
			.filter(p -> p.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) &&
					partitionMap.getPartitionSize(Integer.parseInt(p.getKey().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) <= c)
			.forEach(new Consumer<Entry<Node,Integer>>() {
				public void accept(Entry<Node, Integer> t) {
					Node v = t.getKey();
	
					Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
					Integer partitionSize = partitionMap.getPartitionSize(partitionIndex);
					if (partitionsScores.containsKey(partitionIndex)){
						//x1*w+x2*w+...+xn*w = w*(x1+x2+..+xn)
						partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex)) + t.getValue() 
						* getWeight((double)partitionSize, c));
					} else {
						partitionsScores.put(partitionIndex, (double) t.getValue() *
								getWeight((double) partitionSize, c));
					}
				}
		});
		
		if (partitionsScores.isEmpty()) {
			return new BalancedHeuristic().getIndex(partitionMap, n);
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
						} else { //inverse tie break
							return size2 - size1;
						}
					}
				}).get().getKey();

		return maxPart;

	}



	public abstract Double getWeight(Double partitionSize, Integer c);
	public abstract String getHeuristicName();

}
