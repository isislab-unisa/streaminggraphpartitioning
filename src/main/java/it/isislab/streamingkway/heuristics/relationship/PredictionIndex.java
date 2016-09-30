package it.isislab.streamingkway.heuristics.relationship;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.graphstream.graph.Node;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.KwayDistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class PredictionIndex implements SGPHeuristic, WeightedHeuristic {

	private DistanceFunction dist;
	private boolean parallel;

	public PredictionIndex(boolean parallel) {
		this.parallel = parallel;
	}
	
	public Integer getIndex(PartitionMap partitionMap, Node n) {
		dist = new KwayDistanceFunction();
		Integer capacity = partitionMap.getC();

		double alpha=0;double beta=1;

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		Map<Integer, Double> partitionsScores = new HashMap<Integer, Double>(partitionMap.getK());
		for(int i=1;i<=partitionMap.getK();i++) partitionsScores.put(i,0.0);

		Iterator<Node> nNeighIt = n.getNeighborNodeIterator();
		Node p=null;
		while(nNeighIt.hasNext() && (p=nNeighIt.next())!=null)
		{
			//			System.out.println(n.getId()+"->"+p.getId());
			if(p.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE) && partitionMap.getPartitionSize(Integer.parseInt(p.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))) >= capacity) continue;

			if(p.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)){
				Integer i=Integer.parseInt(p.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
				double score=partitionsScores.get(i)==null?0:partitionsScores.get(i);
				partitionsScores.put(i, score+beta);
			}
			else{

				int predicted_part=getPredictionIndex(partitionMap, p);
				if(predicted_part != -1)
					partitionsScores.put(predicted_part,
							partitionsScores.get(predicted_part)==null?alpha:partitionsScores.get(predicted_part) + alpha);
			}

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
						//						System.out.println("Max "+e1.getKey()+" "+e1.getValue()+" vs "+e2.getKey()+" "+e2.getValue());

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
		//		System.out.println("Assign: "+" \t\t"+n.getId()+" ->"+maxPart);

		return maxPart;
	}


	public final Integer getPredictionIndex(PartitionMap partitionMap, Node p)
	{
		int max=Integer.MIN_VALUE;
		int partid=-1;
		HashMap<Integer,Integer> maxs=new HashMap<Integer,Integer>();
		for (int i = 1; i <= partitionMap.getK(); i++) {

			Collection<Node> part=partitionMap.getPartition(i);

			if(part.size() >= partitionMap.getC()) continue;

			for(Node vp: part)
			{
				if(vp.hasEdgeBetween(p))
				{
					maxs.put(i,maxs.get(i)==null?1:maxs.get(i)+1);
					if(maxs.get(i) > max)
					{
						max=maxs.get(i);
						partid=i;
					}
				}
			}
		}
		if (p.getDegree()/2.0 < max){

			return partid;
		}
		else
			return -1;
	}
	
	public abstract Double getWeight(Double partitionSize, Integer c);
	
	public String getHeuristicName() {
		return " Prediction Index";
	}

}
