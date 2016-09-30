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
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractIndexWithDispersion implements SGPHeuristic, WeightedHeuristic {

	int count = 0;
	
	private SimpleDistanceFunction dist;
	private boolean parallel;
	
	public AbstractIndexWithDispersion(boolean parallel) {
		this.parallel = parallel;
	}
	
	public Integer getIndex(PartitionMap partitionMap, Node n) {
	
		Double T=(double)++count/(double)(partitionMap.getC()*partitionMap.getK());

		dist = new SimpleDistanceFunction();
		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		//score for each neighbour 
		Map<Node, Double> nodeScores = new HashMap<Node, Double>(n.getDegree());

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();

		nNeighIt.forEachRemaining(p -> {

			List<Node> cuv = Dispersion.cuvCalculator(n, p);  
			double emb=(double)cuv.size();
			double disp=(double)Dispersion.getDispersion(n, p, dist);
			//double ndisp = emb < 2 ? 0 : 2*disp/(emb*(emb-1)); 
			double ndisp=emb==0?0:Math.min(1,disp/emb);
			double beta=1-T;
			//			System.out.println(n.getId()+"->"+p.getId()+" \tNDISP:"+ndisp+" \tDISP:"+Dispersion.getDispersion(n, p, dist)+" \tEMB:"+cuv.size()+"\t SCORE:"+(T + beta*(1-ndisp))+" temp: "+T);

			//			if(Math.pow(2, val) > 1) System.out.println("GESOCRISTO");
			//			else System.out.println("A MARONNNNNNNNNNNNNNNNNNNNNNNNNNNN");

			nodeScores.put(p, T + beta*(1-ndisp));

		});

		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		Stream<Entry<Node,Double>> nodeStream = nodeScores.entrySet().stream();
		if (parallel) {
			nodeStream = nodeStream.parallel();
		}
		nodeStream.filter(p -> p.getKey().hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) && 
				partitionMap.getPartitionSize(Integer.parseInt(p.getKey().getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) < c)
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
						//System.out.println("Score "+e1.getKey()+" "+e1.getValue());
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

	public String getHeuristicName() {
		return " Index With Dispersion" ;
	}

	public abstract Double getWeight(Double partitionSize, Integer c);

	
	
}
