package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.WeightedHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.CuvCalculator;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractAbsDispersionBased  implements SGPHeuristic, WeightedHeuristic{

	public DistanceFunction dist = new SimpleDistanceFunction();

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer index = -1;
		Integer c = partitionMap.getC();
		
		if (n.getDegree() == 0) {
			return new BalancedHeuristic().getIndex(g, partitionMap, n);
		}
		
		Map<Node, Integer> nodeScores = new HashMap<>();
		List<Node> nNeighbour = new ArrayList<Node>(n.getDegree());
		
		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		while (nNeighIt.hasNext()) {
			nNeighbour.add(nNeighIt.next());
		}
		for (Node v : nNeighbour) {
			nodeScores.put(v, getDispersion(n, v));
		}
		
		Map<Integer, Double> partitionsScores = new HashMap<>(partitionMap.getK());
		for (Entry<Node, Integer> nSc : nodeScores.entrySet()) {
			if (!nSc.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
				continue;
			}
			Integer partitionIndex = partitionMap.getNodePartition(nSc.getKey());
			if (partitionIndex == null) continue;
			if (partitionsScores.containsKey(partitionIndex)) {
				partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex) + nSc.getValue())
						* getWeight((double)partitionMap.getIntersectionValueParallel(n, partitionIndex), c));
			} else {
				partitionsScores.put(partitionIndex, (double)nSc.getValue());
			}
		}
		if (partitionsScores.isEmpty()) {
			return new BalancedHeuristic().getIndex(g, partitionMap, n);
		}
		double max = Double.NEGATIVE_INFINITY;
		for (Entry<Integer, Double> partitionScore : partitionsScores.entrySet()) {
			if (partitionMap.getPartitionSize(partitionScore.getKey()) >= c) {
				continue;
			}
			if (Double.max(max, partitionScore.getValue()) == partitionScore.getValue()) {
				max = partitionScore.getValue();
				index = partitionScore.getKey();
			}
		}
		
		return index == -1 ? new BalancedHeuristic().getIndex(g, partitionMap, n) : index;
		
	}
	
	private Integer getDispersion(Node u, Node v) {
		Integer dispValue = 0;
		List<Node> cuv = CuvCalculator.cuvCalculator(u, v);
		int l = cuv.size();
		for (int i = 0; i < l; i++) {
			for (int j = i+1; j < l; j++) {
				dispValue += dist.getDistance(cuv.get(i), cuv.get(j), u, v);
			}
		}
		return dispValue;
	}

	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
