package it.isislab.streamingkway.heuristics.relationship;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractDispersionPredict implements SGPHeuristic, WeightedHeuristic {

	private boolean parallel;
	private int indexTieBrek;
	
	public AbstractDispersionPredict(boolean parallel) {
		this.parallel = parallel;
	}
	
	public abstract Double getWeight(Double partitionSize, Integer c);
	
	public Integer getIndex(PartitionMap partitionMap, Node n) {

		Integer c = partitionMap.getC();

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<>(partitionMap.getK());
		for (int i = 1; i <= partitionMap.getK(); i++) {
			if(partitionMap.getPartition(i).size() >= c)
			{
				continue;
			}
			int y=0;
			
			for (int j = 1; j <= partitionMap.getK(); j++) {
				int tmp=partitionMap.getIntersectionValue(n,j);
				if(j!=i) y+=tmp;
			}
			int x=partitionMap.getIntersectionValue(n, i);
			int score=-x+y;//-partitionMap.getIntersectionValue(n, i);

			Iterator<Node> pOFn=n.getNeighborNodeIterator();
			Node p=null;
			while(pOFn.hasNext())
			{
				p=pOFn.next();

				if(!p.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))
					score+=phiNeighbor(partitionMap, n, p, i);
			}
			partitionsScores.put(i, (double)score);
//			System.out.println(n+" = "+partitionsScores);

		}

		if (partitionsScores.isEmpty()) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		Stream<Entry<Integer, Double>> strScore = partitionsScores.entrySet().stream();
		if (parallel) {
			strScore = strScore.parallel();
		}

		Integer maxPart = strScore
				.min(new Comparator<Entry<Integer, Double>>() {
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
							return size1 >= size2 ? 1 : -1;
						}
					}
				}).get().getKey();
//		System.out.println(n+"->"+maxPart);
		return maxPart;
		
	}
	

	private int phiNeighbor(PartitionMap partitionMap, Node n, Node v, int i)
	{
		indexTieBrek=-1;
		int index=getIndexLDG(partitionMap, v);
		if(indexTieBrek==-1 || partitionMap.getIntersectionValue(v, index)==0){
			return 0;
		}
//		if(partitionMap.getIntersectionValue(v, index)==0) return 0;
		int totedges=0;
		int x=partitionMap.getIntersectionValue(v,i);
		int y=0;
		
		for (int j = 1; j <= partitionMap.getK(); j++) {
			int tmp=partitionMap.getIntersectionValue(v,j);
			totedges+=tmp;
			if(j!=i) y+=tmp;
		}
		
		if(totedges < (n.getGraph().getEdgeCount()/n.getGraph().getNodeCount())/2) return 0;
		
		if(i != index)
		{
			//return 1 +  x - y;
			return - x + y;
		}else{ //i== index
			int yindex=partitionMap.getIntersectionValue(v,index);
			return 1 + x - yindex + (y - yindex);
			//return y - x; 
		}
		
	}
	
public Integer getIndexLDG(PartitionMap partitionMap, Node n)  {
		
		Map<Integer,Collection<Node>> partitions = partitionMap.getPartitions();
		Integer c = partitionMap.getC();

		Stream<Entry<Integer, Collection<Node>>> str = partitions.entrySet().stream();
		if (parallel) {
			str = str.parallel();
		}
		
		Integer maxIndex = str
				.filter(p -> p.getValue().size() <= c)
				.max(new Comparator<Map.Entry<Integer,Collection<Node>>>() {
					public int compare(Map.Entry<Integer,Collection<Node>> p1,
							Map.Entry<Integer,Collection<Node>> p2) {
						int p1size = p1.getValue().size();
						int p2size = p2.getValue().size();
					
						double intersect1 = partitionMap.getIntersectionValueParallel(n, p1.getKey());
						double intersect2 = partitionMap.getIntersectionValueParallel(n, p2.getKey());
						double w1 = getWeight((double)p1size, c);
						double w2 = getWeight((double)p2size, c);
						intersect1 *= w1;
						intersect2 *= w2;
						if (intersect1 > intersect2) {
							return 1;
						} else if (intersect1 < intersect2) {
							return -1;
						} else { //tie break
							if(p1size >= p2size)
							{
								indexTieBrek=p2.getKey();
								return -1;
							}else{
								indexTieBrek=p1.getKey();
								return 1;
							}
						}
					}
				}).get().getKey();
		return maxIndex;
	}

	public String getHeuristicName() {
		return " Dispersion Predict";
	}
	
	

}
