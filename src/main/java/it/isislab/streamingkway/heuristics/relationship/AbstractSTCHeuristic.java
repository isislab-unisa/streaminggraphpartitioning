package it.isislab.streamingkway.heuristics.relationship;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractSTCHeuristic implements SGPHeuristic, WeightedHeuristic {

	private boolean parallel;
	
	public AbstractSTCHeuristic(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		double alpha=1,beta=2;
		Integer c = partitionMap.getC();
		Map<Integer, Double> partitionsScores = new ConcurrentHashMap<Integer, Double>(partitionMap.getK());

		if (n.getDegree() == 0) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}
		for (int i = 1; i <= partitionMap.getK(); i++) {

			if(partitionMap.getPartition(i).size() >= c) continue;
			List<Node> neighborsOnI=partitionMap.getIntersectionNodes(n,i);
			labelEdgesSTC(partitionMap, n, neighborsOnI);

			double scorei=0.0;
			for(Node p: neighborsOnI)
			{
				scorei+=((boolean)n.getEdgeBetween(p).getAttribute("stc"))?alpha:beta;
				//System.out.println(n.getEdgeBetween(p)+" ->"+((boolean)n.getEdgeBetween(p).getAttribute("stc")));
			}
			partitionsScores.put(i, scorei);

			//System.out.println(i+ " "+ scorei);


		}

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
		//	System.out.println("\t"+n +" -> "+maxPart);
		return maxPart;
	}

	private void labelEdgesSTC(PartitionMap partitionMap, Node n,List<Node> nOnI)
	{

		List<Node> neighborsOnI = new ArrayList<Node>(nOnI);

		//		Iterator<Node> in=n.getNeighborNodeIterator();
		//		while(in.hasNext()){
		//			Node nv= in.next();
		//			if (!neighborsOnI.contains(nv))
		//				neighborsOnI.add(nv);
		//		}

		for(Node p:neighborsOnI)
		{
			Edge e=n.getEdgeBetween(p);
			e.setAttribute("stc", new Boolean(true));

		}
		while(!checkSTCproperty(n,neighborsOnI))
		{
			int min=Integer.MAX_VALUE;
			Node node=null;
			for(Node p:neighborsOnI)
			{
				int degree=0;
				Edge e=n.getEdgeBetween(p);
				if(!(boolean)e.getAttribute("stc")) continue;
				Iterator<Node> ite=p.getNeighborNodeIterator();
				Node pv=null;
				while(ite.hasNext())
				{
					pv=ite.next();
					if(neighborsOnI.contains(pv))
					{
						degree++;
					}

				}
				if(degree < min)
				{
					min=degree;
					node=p;
				}

			}
			Edge e=n.getEdgeBetween(node);
			e.setAttribute("stc", new Boolean(false));
		}//end while
	}
	private boolean checkSTCproperty(Node n,List<Node> neighborsOnI) {

		for(Node u:neighborsOnI)
		{
			for(Node v: neighborsOnI)
			{
				Edge e1 = u.getEdgeBetween(v);
				Edge e2 = n.getEdgeBetween(v);
				Edge e3 = n.getEdgeBetween(u);
				if(u!=v && e1==null && ((boolean)e2.getAttribute("stc") && (boolean)e3.getAttribute("stc")))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public String getHeuristicName() {
		return " STC";
	}

	public abstract Double getWeight(Double partitionSize, Integer c);
	
}
