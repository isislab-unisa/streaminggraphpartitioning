package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.graphstream.graph.Node;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractRecursiveDispersionBased implements SGPHeuristic, WeightedHeuristic {

	protected boolean parallel;
	public AbstractRecursiveDispersionBased(boolean parallel) {
		this.parallel = parallel;
	}

	private static final int ITERATION_TIME = 4;
	private DistanceFunction dist = new SimpleDistanceFunction();

	public final Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}

		List<Node> nNeighbours = new ArrayList<Node>(n.getDegree());
		n.getNeighborNodeIterator().forEachRemaining(p -> nNeighbours.add(p));

		Map<Node, Double> xNodes = getDispersion(nNeighbours, n);

		Map<Integer, Double> partitionsScore = new ConcurrentHashMap<>(partitionMap.getK());
		Stream<Entry<Node,Double>> str = xNodes.entrySet().stream();
		if (parallel) {
			str = str.parallel();
		}
		str
			.filter(p -> p.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) &&
				partitionMap.getPartitionSize(Integer.parseInt(p.getKey().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) <= c)
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
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}

		Stream<Entry<Integer,Double>> scoreStr = partitionsScore.entrySet().stream();
		if (parallel) {
			scoreStr = scoreStr.parallel();
		}
		Integer maxPartIndex = scoreStr
				.max(new Comparator<Entry<Integer, Double>>() {

					public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
						Double p1score = o1.getValue();
						Double p2score = o2.getValue();
						if (p1score > p2score) {
							return 1;
						} else if (p1score < p2score) {
							return -1;
						} else { 
							return partitionMap.getPartition(o1.getKey()).size() >=
									partitionMap.getPartition(o2.getKey()).size() ? -1 : 1;
						}
					}
				}).get().getKey();

		return maxPartIndex;
	}

	public String getHeuristicName() {
		return "Recursive Dispersion Based";
	};
	public abstract Double getWeight(Double intersectNumber, Integer c);

	private Map<Node,Double> getDispersion(List<Node> uNeighbour, Node n) {
		Map<Node, Double> xNodes = new ConcurrentHashMap<>(n.getDegree());
		Map<Node, List<Node>> cuvs = new ConcurrentHashMap<>(n.getDegree());

		for (int iteration = ITERATION_TIME; iteration-- > 0;) {
			Stream<Node> str = uNeighbour.stream();
			if (parallel) {
				str = str.parallel();
			}
			str
			.forEach(new Consumer<Node>() {

				public void accept(Node v) 	{
					//cuv contains all uv common neighbour
					List<Node> cuv = null;
					if (cuvs.containsKey(v)) {
						cuv = cuvs.get(v);
					} else {
						cuv = Dispersion.cuvCalculator(v, n);
						cuvs.put(v, cuv);
					}

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
							pt2 += (dist.getDistance(cuv.get(i), cuv.get(i), n, v) * xs * xt);
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
