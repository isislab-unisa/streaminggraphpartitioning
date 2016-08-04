package it.isislab.streamingkway.heuristics.relationship;

import java.util.Comparator;
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
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}

		Map<Node, Double> nodeScores = new ConcurrentHashMap<>();

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		nNeighIt.forEachRemaining(new Consumer<Node>() {
			public void accept(Node v) {
				List<Node> cuv = Dispersion.cuvCalculator(n, v);
				int emb = cuv.size();
				double disp = Dispersion.getDispersion(n,v,dist,cuv);
				nodeScores.put(v, Math.pow(disp + B, A) / (double) emb + C);
			}
		});

		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		nodeScores.entrySet().parallelStream()
			.forEach(new Consumer<Entry<Node,Double>>() {

				public void accept(Entry<Node, Double> t) {
					Node v = t.getKey();
					Double score = t.getValue();
					if (v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
						Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
						Integer partitionSize = partitionMap.getPartitionSize(partitionIndex);
						if (partitionSize > c) {
							return;
						}
						if (partitionsScores.containsKey(partitionIndex)) {
							partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex)) + 
									score * getWeight((double) partitionSize,c));
						} else {
							partitionsScores.put(partitionIndex, score * 
									getWeight((double) partitionSize,c));
						}
					} 
				}
				
			});

		if (partitionsScores.isEmpty()) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
		Integer maxPart = partitionsScores.entrySet().parallelStream().max(new Comparator<Entry<Integer, Double>>() {

			public int compare(Entry<Integer, Double> p1, Entry<Integer, Double> p2) {
				Double p1score = p1.getValue();
				Double p2score = p2.getValue();
				if (Double.max(p1score, p2score) == p1score) {
					return 1;
				} else if (Double.max(p1score, p2score) == p2score) {
					return -1;
				} else { //tie break
					return partitionMap.getPartitionSize(p1.getKey()) - partitionMap.getPartitionSize(p2.getKey());
				}
			}
			
		}).get().getKey();

		return maxPart;
	}
	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
