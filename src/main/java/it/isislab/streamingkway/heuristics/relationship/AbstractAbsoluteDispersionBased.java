package it.isislab.streamingkway.heuristics.relationship;

import java.util.Comparator;
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
public abstract class AbstractAbsoluteDispersionBased implements SGPHeuristic, WeightedHeuristic {

	private Double A = 0.61;
	private Double B = 0.0;
	private Double C = 5.0;
	protected boolean parallel;

	private DistanceFunction dist = new SimpleDistanceFunction();
	
	public AbstractAbsoluteDispersionBased(boolean parallel) {
		this.parallel = parallel;
	}

	public AbstractAbsoluteDispersionBased(Double A, Double B, Double C, boolean parallel) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.parallel = parallel;
	}
	
	public final Integer getIndex(PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}

		Map<Node, Double> nodeScores = new ConcurrentHashMap<>();

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		nNeighIt.forEachRemaining(new Consumer<Node>() {
			public void accept(Node v) {
				List<Node> cuv = Dispersion.cuvCalculator(n, v);
				int emb = cuv.size();
				double disp = Dispersion.getDispersion(n,v,dist,cuv);
				nodeScores.put(v, (Math.pow(disp +  B, A) / ((double) emb + C)));
			}
		});
		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		Stream<Entry<Node,Double>> str = nodeScores.entrySet().stream();
		if (parallel) {
			str = str.parallel();
		}
		str
			.filter(p -> p.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) &&
				partitionMap.getPartitionSize(Integer.parseInt(p.getKey().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) <= c)
			.forEach(new Consumer<Entry<Node,Double>>() {
	
				public void accept(Entry<Node, Double> t) {
					Node v = t.getKey();
					Double score = t.getValue();
	
					Integer partitionIndex = Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
					Integer partitionSize = partitionMap.getPartitionSize(partitionIndex);
					if (partitionsScores.containsKey(partitionIndex)) {
						partitionsScores.put(partitionIndex, (partitionsScores.get(partitionIndex)) + 
								score * getWeight((double) partitionSize,c));
					} else {
						partitionsScores.put(partitionIndex, score * 
								getWeight((double) partitionSize,c));
					}
				} 

		});

		if (partitionsScores.isEmpty()) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		Stream<Entry<Integer,Double>> strScore = partitionsScores.entrySet().stream();
		if (parallel) {
			strScore = strScore.parallel();
		}
		Integer maxPart = strScore
			.max(new Comparator<Entry<Integer, Double>>() {
				public int compare(Entry<Integer, Double> p1, Entry<Integer, Double> p2) {
					Double p1score = p1.getValue();
					Double p2score = p2.getValue();
					if (p1score > p2score) {
						return 1;
					} else if (p1score < p2score) {
						return -1;
					} else { //inverse tie break
						return partitionMap.getPartition(p1.getKey()).size() >=
								partitionMap.getPartition(p2.getKey()).size() ? -1 : 1;
					}
				}

		}).get().getKey();

		return maxPart;
	}
	public Double getA() {
		return A;
	}
	public void setA(Double a) {
		A = a;
	}
	public Double getB() {
		return B;
	}
	public void setB(Double b) {
		B = b;
	}
	public Double getC() {
		return C;
	}
	public void setC(Double c) {
		C = c;
	}
	public abstract Double getWeight(Double intersectNumber, Integer c);
	public String getHeuristicName() {
		return "Absolute Dispersion Based";
	}

}
