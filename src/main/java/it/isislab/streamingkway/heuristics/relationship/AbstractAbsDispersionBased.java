package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
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
		Integer index = -1;
		Integer c = partitionMap.getC();
		
		if (n.getDegree() == 0) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
		
		//FIXME USE PARALLEL REDUCTION!
		List<Node> nNeighbour = new ArrayList<Node>(n.getDegree());
		Map<Node, Integer> nodeScores = new ConcurrentHashMap<Node, Integer>(n.getDegree());
		
		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		while (nNeighIt.hasNext()) {
			nNeighbour.add(nNeighIt.next());
		}
		nNeighbour.parallelStream()
			.forEach(p -> nodeScores.put(p, Dispersion.getDispersion(p, n, dist)));

		
		//FIXME USE AGGREGATION FUNCTIONS
		Map<Integer, Double> partitionsScores = new HashMap<>(partitionMap.getK());
		for (Entry<Node, Integer> nSc : nodeScores.entrySet()) {
			if (!nSc.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
				continue;
			}
			Integer partitionIndex = partitionMap.getNodePartition(nSc.getKey());
			if (partitionIndex == null) continue;
			if (partitionsScores.containsKey(partitionIndex)) {
				partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex)) + nSc.getValue()
						* getWeight((double)partitionMap.getIntersectionValueParallel(n, partitionIndex), c));
			} else {
				partitionsScores.put(partitionIndex, (double)nSc.getValue()* 
						getWeight((double)partitionMap.getIntersectionValueParallel(n, partitionIndex), c));
			}
		}
		if (partitionsScores.isEmpty()) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
		double max = Double.NEGATIVE_INFINITY;
		for (Entry<Integer, Double> partitionScore : partitionsScores.entrySet()) {
			Integer key = partitionScore.getKey();
			if (partitionMap.getPartitionSize(key) > c) {
				continue;
			}
			double score = partitionScore.getValue();
			if (Double.max(max, score) == score) {
				max = score;
				index = key;
			}
		}
		
		return index == -1 ? new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n) : index;
		
	}
	


	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
