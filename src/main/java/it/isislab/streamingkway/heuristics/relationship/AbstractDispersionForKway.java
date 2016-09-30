package it.isislab.streamingkway.heuristics.relationship;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.Kway2DistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractDispersionForKway implements SGPHeuristic, WeightedHeuristic {
	
	private boolean parallel;
	private DistanceFunction dist;
	
	public AbstractDispersionForKway(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		dist = new Kway2DistanceFunction(partitionMap);
		Integer capacity = partitionMap.getC();
		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		Map<Integer, Double> partitionsScores = new HashMap<Integer, Double>(partitionMap.getK());

		for(int i=1;i<=partitionMap.getK();i++) partitionsScores.put(i,0.0);

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		Node p=null;
		while(nNeighIt.hasNext() && (p=nNeighIt.next())!=null)
		{
			//System.out.println(n.getId()+"->"+p.getId());
			if(!p.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) || partitionMap.getPartitionSize(Integer.parseInt(p.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) >= capacity) continue;

			Integer i=Integer.parseInt(p.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));

			List<Node> cuv = Dispersion.cuvCalculator(n, p); 
			int embeddedness=cuv.size();
			double dispersion_normalized = embeddedness == 0 ? 1 : (Dispersion.getDispersion(n, p, dist, cuv)/(embeddedness));
			//(embeddedness + (Dispersion.getDispersion(n, p, dist, cuv)/(embeddedness))/2*embeddedness);
			double score=partitionsScores.get(i)==null?0:partitionsScores.get(i);
			//
			//			int maxdegree= Math.max(n.getDegree(),p.getDegree());
			//			double normembeddedness = embeddedness *1.0/maxdegree;

			partitionsScores.put(i, score+( /*1-dispersion_normalized*/ dispersion_normalized));

			//			System.out.println(" \t d:"+Dispersion.getDispersion(p, n, dist, cuv)+" emb:"+cuv.size()+" score:"+partitionsScores.get(i));
			//System.out.println("\t "+cuv);
		}
		boolean noScore=true;
		for(int i=1;i<=partitionMap.getK();i++)
		{
			if(partitionsScores.get(i)!=0)
			{
				noScore=false;
				break;
			}
		}
		if (noScore) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}

		Stream<Entry<Integer, Double>> strScore = partitionsScores.entrySet().stream();
		Integer maxPart = 
				strScore.filter(e -> partitionMap.getPartitionSize(e.getKey()) < capacity)
				.max(new Comparator<Entry<Integer, Double>>() {
					public int compare(Entry<Integer, Double> e1, Entry<Integer, Double> e2) {
						//System.out.println("Max "+e1.getKey()+" "+e1.getValue()+" vs "+e2.getKey()+" "+e2.getValue());

						Integer size1 = partitionMap.getPartitionSize(e1.getKey());
						Integer size2 = partitionMap.getPartitionSize(e2.getKey());
						Double score1 = getWeight((double)size1, capacity) * (e1.getValue());
						Double score2 = getWeight((double)size2, capacity) * (e2.getValue());
						if (score1 > score2) {
							return 1;
						} else if (score1 < score2) {
							return -1;
						} else { 
							return size1 <= size2 ? -1 : 1;
						}
					}
				}).get().getKey();
		//System.out.println("Assign: "+" \t\t"+n.getId()+" ->"+maxPart);

		return maxPart;
	}

	
	public abstract Double getWeight(Double partitionSize, Integer c);
	
	public String getHeuristicName() {
		return " Dispersion for kway";
	}

	

}
