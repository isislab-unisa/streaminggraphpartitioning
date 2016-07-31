package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.graphstream.graph.Edge;
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

public abstract class AbstractRecursiveDispersionBased implements SGPHeuristic, WeightedHeuristic {


	private static final int ITERATION_TIME = 4;
	private DistanceFunction dist = new SimpleDistanceFunction();

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer index = -1; 
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}

		List<Node> nNeighbours = new ArrayList<Node>(n.getDegree());
		Iterator<Edge> edgeIt = n.getEachEdge().iterator();
		while (edgeIt.hasNext()) {
			Edge t = edgeIt.next();
			Node u = t.getOpposite(n);
			//TODO check if it u must be already partitioned
			nNeighbours.add(u);
		}
		edgeIt = null;

		if (nNeighbours.isEmpty()) { //it does not have partitioned neighbour
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}

		Map<Node, Double> xNodes = getDispersion(nNeighbours, n);

		Map<Integer, Collection<Node>> partitions = partitionMap.getPartitions();
		Map<Integer, Double> partitionsScore = new HashMap<>(partitions.size());
		for (int i = 1; i <= partitions.size(); i++) {
			partitionsScore.put(i,0.0);
		}

		for (Node v: xNodes.keySet()) {
			if (v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) {
				Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
				Integer intersectNodes = partitionMap.getIntersectionValue(n, partitionIndex);
				partitionsScore.put(partitionIndex, (partitionsScore.get(partitionIndex) +
						xNodes.get(v) * getWeight((double)intersectNodes, c)));
			}
		}
		double max = Double.NEGATIVE_INFINITY;
		//force garbage collector
		xNodes = null;
		nNeighbours = null;

		for (Entry<Integer,Double> score : partitionsScore.entrySet()) {
			if (partitionMap.getPartitionSize(score.getKey()) > c) {
				continue;
			}
			double partitionValue = score.getValue();
			if (Double.max(max, partitionValue) == partitionValue) {
				max = partitionValue;
				index = score.getKey();
			}
		}

		return index == -1 ? new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n) : index;
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
