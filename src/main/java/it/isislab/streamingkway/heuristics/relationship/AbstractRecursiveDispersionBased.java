package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.LinearWeightedDeterministicGreedy;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractRecursiveDispersionBased implements SGPHeuristic, WeightedHeuristic {


	private static final int ITERATION_TIME = 4;
	private DistanceFunction dist = new SimpleDistanceFunction();

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic().getIndex(partitionMap, n);
		}

		List<Node> nNeighbours = new ArrayList<Node>(n.getDegree());
		n.getNeighborNodeIterator().forEachRemaining(p -> nNeighbours.add(p));

		Map<Node, Double> xNodes = getDispersion(nNeighbours, n);

		Map<Integer, Double> partitionsScore = new ConcurrentHashMap<>(partitionMap.getK());
		xNodes.entrySet().parallelStream()
			.filter(p -> p.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))
			.filter(p -> partitionMap.getPartitionSize(Integer.parseInt(p.getKey().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) <= c)
			.forEach(new Consumer<Entry<Node,Double>>() {
				public void accept(Entry<Node, Double> t) {
					Node v = t.getKey();
					Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
					Integer partitionSize = partitionMap.getPartitionSize(partitionIndex);
					if (partitionsScore.containsKey(partitionIndex)) {
						partitionsScore.put(partitionIndex, (partitionsScore.get(partitionIndex)) +
								t.getValue() * getWeight((double) partitionSize,c));
					} else {
						partitionsScore.put(partitionIndex,
								t.getValue() * getWeight((double) partitionSize,c));
					}
				}

		});

		if (partitionsScore.isEmpty()) {
			return new LinearWeightedDeterministicGreedy().getIndex(partitionMap, n);
		}

		Integer maxPartIndex = partitionsScore.entrySet()
				.parallelStream().max(new Comparator<Entry<Integer, Double>>() {

					public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
						Double p1score = o1.getValue();
						Double p2score = o2.getValue();
						if (Double.max(p1score, p2score) == p1score) {
							return 1;
						} else if (Double.max(p1score, p2score) == p2score) {
							return -1;
						} else { //inverse tie break
							return partitionMap.getPartitionSize(o2.getKey()) - 
									partitionMap.getPartitionSize(o1.getKey());
						}
					}
				}).get().getKey();

		return maxPartIndex;
	}

	public abstract String getHeuristicName();
	public abstract Double getWeight(Double intersectNumber, Integer c);

	private Map<Node,Double> getDispersion(List<Node> uNeighbour, Node u) {
		Map<Node, Double> xNodes = new ConcurrentHashMap<>(u.getDegree());
		for (int iteration = 0; iteration < ITERATION_TIME; iteration++) {
			uNeighbour.parallelStream().forEach(new Consumer<Node>() {

				public void accept(Node v) 	{
					//cuv contains all uv common neighbour
					List<Node> cuv = Dispersion.cuvCalculator(u, v);

					//calculate pt.1
					Double pt1 = 0.0;
					for (Node w : cuv) {
						if (xNodes.containsKey(w)) {
							pt1 += xNodes.get(w)*xNodes.get(w);
						} else {
							pt1 += 1.0;
							xNodes.put(w, 1.0);
						}
					}
					//calculate pt.2
					Double pt2 = 0.0;
					int size = cuv.size();
					for (int i = 0; i < size; i++) {
						for (int j = i+1; j < size; j++) {
							Node s = cuv.get(i);
							Node t = cuv.get(j);
							double xs = 1.0;
							if (xNodes.containsKey(s)) {
								xs = xNodes.get(s);
							} else{
								xNodes.put(s, 1.0);
							}
							double xt = 1.0;
							if (xNodes.containsKey(s)) {
								xt = xNodes.get(t);
							} else{
								xNodes.put(t, 1.0);
							}
							pt2 += (dist.getDistance(cuv.get(i), cuv.get(i), u, v) * xs * xt);
						}
					}
					//calculate all
					double xv = (pt1 + 2*pt2)/cuv.size();
					xNodes.put(v, xv);

				}
			}); 
		}

		return xNodes;
	}


}
