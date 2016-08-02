package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public abstract class AbstractNormDispersionBased implements SGPHeuristic, WeightedHeuristic {

	private static final Double A = 0.61;
	private static final Double B = 0.0;
	private static final Double C = 5.0;

	private DistanceFunction dist = new SimpleDistanceFunction();

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer index = -1;
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}

		Map<Node, Double> nodeScores = new ConcurrentHashMap<>();
		List<Node> nNeighbour = new ArrayList<Node>(n.getDegree());

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		while (nNeighIt.hasNext()) {
			nNeighbour.add(nNeighIt.next());
		}

		nNeighbour.parallelStream().forEach(new Consumer<Node>() {
			public void accept(Node v) {
				List<Node> cuv = Dispersion.cuvCalculator(n, v);
				int emb = cuv.size();
				double disp = Dispersion.getDispersion(n, v, dist, cuv);
				nodeScores.put(v, Math.pow(disp + B, A) / (double)emb + C);
			}

		});

		Map<Integer, Double> partitionsScores = new HashMap<>(partitionMap.getK());
		for (Node v: nodeScores.keySet()) {
			if (v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
				Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
				if (partitionsScores.containsKey(partitionIndex)) {
					partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex) +
							nodeScores.get(v) * 
							getWeight((double)partitionMap.getPartitionSize(partitionIndex),c)));
				}
			}
		}

		if (partitionsScores.isEmpty()) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
		double max = Double.NEGATIVE_INFINITY;
		for (Entry<Integer, Double> partitionScore : partitionsScores.entrySet()) {
			if (partitionMap.getPartitionSize(partitionScore.getKey()) > c) {
				continue;
			}
			if (Double.max(max, partitionScore.getValue()) == partitionScore.getValue()) {
				max = partitionScore.getValue();
				index = partitionScore.getKey();
			}
		}

		return index == -1 ? new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n) : index;
	}
	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
