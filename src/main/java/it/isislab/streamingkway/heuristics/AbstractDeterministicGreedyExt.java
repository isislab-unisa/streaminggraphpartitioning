package it.isislab.streamingkway.heuristics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class AbstractDeterministicGreedyExt implements SGPHeuristic, WeightedHeuristic {

	protected boolean parallel;
	private final static Double ALPHA = 1.0;
	
	public AbstractDeterministicGreedyExt(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n)  {
		
		Integer c = partitionMap.getC();
		Map<Integer, Double> partitionScore = new ConcurrentHashMap<Integer, Double>();
		ArrayList<Node> nNeigh = new ArrayList<Node>(n.getDegree());
		n.getNeighborNodeIterator().forEachRemaining(p -> nNeigh.add(p));
		
		for (int i = 1; i <= partitionMap.getK(); i++) {
			partitionScore.put(i, 0.0);
		}
		
		nNeigh.parallelStream()
			.filter(v -> v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))
			.forEach(v -> {
				int vInd = Integer.parseInt( v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
				if (partitionScore.containsKey(vInd)) {
					partitionScore.put(vInd, 1.0);
				} else {
					double vIndScore = partitionScore.get(vInd);
					partitionScore.put(vInd, vIndScore + 1);
				}
		});
		
		nNeigh.parallelStream()
			.filter(v -> !v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))
			.forEach(v -> {
				ArrayList<Node> vNeigh = new ArrayList<>(v.getDegree());
				v.getNeighborNodeIterator().forEachRemaining(u -> vNeigh.add(u));
				vNeigh.parallelStream()
					.filter(u -> u.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))
					.filter(u -> !u.equals(n))
					.forEach(u -> {
						int uInd = Integer.parseInt(u.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
						if (partitionScore.containsKey(uInd)) {
							partitionScore.put(uInd, 1.0);
						} else {
							double vIndScore = partitionScore.get(uInd);
							partitionScore.put(uInd, vIndScore + 1);
						}
					});
				
			});
		
		Integer maxIndex = partitionScore.entrySet().parallelStream()
				.filter(p -> partitionMap.getPartitionSize(p.getKey()) <= c)
				.max(new Comparator<Entry<Integer, Double>>() {

					public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
						double w1 = getWeight((double)partitionMap.getPartitionSize(o1.getKey()), partitionMap.getC());
						double w2 = getWeight((double)partitionMap.getPartitionSize(o2.getKey()), partitionMap.getC());
						double score1 = w1*o1.getValue();
						double score2 = w2*o2.getValue();
						if (score1 > score2) {
							return 1;
						} else if (score1 < score2) {
							return -1;
						} else  {
							int size1 = partitionMap.getPartitionSize(o1.getKey());
							int size2 = partitionMap.getPartitionSize(o2.getKey());
							return size1 >= size2 ? -1 : 1;
						}
					}
					
				})
				.get()
				.getKey();

		return maxIndex > 0 ? maxIndex : new BalancedHeuristic(parallel).getIndex(partitionMap, n);
	}

	public Double getWeight(Double partitionSize, Integer c) {
		return 1.0;
	}; 
	public String getHeuristicName() {
		return "Deterministic Greedy";
	}

}
