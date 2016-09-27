package it.isislab.streamingkway.heuristics.relationship;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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

public abstract class AbstractAbsDispersionBased  implements SGPHeuristic, WeightedHeuristic{

	protected boolean parallel;
	public AbstractAbsDispersionBased(boolean parallel) {
		this.parallel = parallel;
	}
	
	public DistanceFunction dist = new SimpleDistanceFunction();
	

	public final Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		//score for each neighbour 
		Map<Node, Double> nodeScores = new HashMap<Node, Double>(n.getDegree());

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();

		//1 + disp(u,v) in order to mix DG with ADB
		nNeighIt.forEachRemaining(p -> {
				List<Node> cuv = Dispersion.cuvCalculator(p, n);  
				double val = cuv.size() == 0 ? 0 : (Dispersion.getDispersion(p, n, dist, cuv)/(cuv.size())); 
						
				nodeScores.put(p,1- val);
//			nodeScores.put(p,1);
		});
		
		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		Stream<Entry<Node,Double>> nodeStream = nodeScores.entrySet().stream();
		if (parallel) {
			nodeStream = nodeStream.parallel();
		}
		nodeStream.filter(p -> p.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) && partitionMap.getPartitionSize(Integer.parseInt(p.getKey().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) <= c)
			.forEach(new Consumer<Entry<Node,Double>>() {
				public void accept(Entry<Node, Double> t) {
					Node v = t.getKey();
					Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
					if (partitionsScores.containsKey(partitionIndex)){
						    partitionsScores.put(partitionIndex, 
						    		((partitionsScores.get(partitionIndex)) + t.getValue()) );
					} else {
						partitionsScores.put(partitionIndex, (double) t.getValue());
					}
				}
		});
		

		if (partitionsScores.isEmpty()) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		Stream<Entry<Integer, Double>> strScore = partitionsScores.entrySet().stream();
		if (parallel) {
			strScore = strScore.parallel();
		}
		
		Integer maxPart = strScore
				.max(new Comparator<Entry<Integer, Double>>() {
					public int compare(Entry<Integer, Double> e1, Entry<Integer, Double> e2) {
//						Integer intersection1 = partitionMap.getIntersectionValue(n, e1.getKey());
//						Integer intersection2 = partitionMap.getIntersectionValue(n, e2.getKey());
						Integer size1 = partitionMap.getPartitionSize(e1.getKey());
						Integer size2 = partitionMap.getPartitionSize(e2.getKey());
						Double score1 = getWeight((double)size1, c) *(e1.getValue());
						Double score2 = getWeight((double)size2, c)*(e2.getValue());
						if (score1 > score2) {
							return 1;
						} else if (score1 < score2) {
							return -1;
						} else { 
							return size1 >= size2 ? -1 : 1;
						}
					}
				}).get().getKey();

		return maxPart;

	}



	public abstract Double getWeight(Double partitionSize, Integer c);
	public String getHeuristicName() {
		return "Absolute Dispersion Based";
	}

}
