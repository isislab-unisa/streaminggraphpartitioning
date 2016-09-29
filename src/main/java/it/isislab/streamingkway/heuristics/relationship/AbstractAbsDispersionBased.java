package it.isislab.streamingkway.heuristics.relationship;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.Kway2DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.KwayDistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;
import it.isislab.streamingkway.heuristics.weight.WeightedHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


public abstract class AbstractAbsDispersionBased  implements SGPHeuristic, WeightedHeuristic{

	protected boolean parallel;
	public AbstractAbsDispersionBased(boolean parallel) {
		this.parallel = parallel;
	}
	public final Integer getIndex(PartitionMap partitionMap, Node n) {
		
		return getIndexWithDispersion(partitionMap, n);
	}
	public final Integer getIndexWithLabel(PartitionMap partitionMap, Node n) {
		setupNodes(n.getGraph(), partitionMap);
		int max_ite=5;
		while(!runLabelPropagation(n.getGraph(),n) && (max_ite--)>0);

		int index=n.getAttribute("label");
		if(index==0)
		{
			index=new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}

		System.out.println(n+" -> "+index);
		return index;
	}
	private int getBalancedSetupLabel(Node u,PartitionMap map)
	{
		if(u.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))
		{
			if(map.getPartition(Integer.parseInt(u.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE))).size() >= map.getC())
			{
				return -1;
			}else
				return Integer.parseInt(u.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
		}else 
			return 0;
	}
	private void setupNodes(Graph g, PartitionMap map)
	{
		for(Node u : g.getNodeSet())
		{ 
			u.setAttribute("label",getBalancedSetupLabel(u,map));
			u.setAttribute("tmplabel",u.getAttribute("label"));
		}
	}
	private boolean runLabelPropagation(Graph g, Node toSet)
	{
		boolean isEnded=true;
		
		Set<Node> nodes=new HashSet<Node>();
		Iterator<Node> uIterator = toSet.getNeighborNodeIterator();
		while (uIterator.hasNext()) {
			Node w = uIterator.next();
			nodes.addAll(Dispersion.cuvCalculator(toSet, w));
		}
		for(Node u : nodes/*g.getNodeSet()*/)
		{
			
			if((int)u.getAttribute("label") == -1) continue;

			HashMap<Integer,Integer> labelScores=new HashMap<Integer,Integer>();
			Iterator<Node> iteNeighbors=u.getNeighborNodeIterator();

			if((int)u.getAttribute("label") != 0) labelScores.put(u.getAttribute("label"),1);

			Node p=null;
			int max=Integer.MIN_VALUE;
			Integer maxlabel=0;
			while(iteNeighbors.hasNext())
			{
				p=iteNeighbors.next();
				Integer plabel=p.getAttribute("label");
				if (plabel > 0){
					labelScores.put(plabel,labelScores.get(plabel)==null?1:labelScores.get(plabel)+1);
					if(labelScores.get(plabel) > max)
					{
						max=labelScores.get(plabel);
						maxlabel=plabel;
					}
				}
			}
			u.setAttribute("tmplabel", maxlabel);
		}
		for(Node u : g.getNodeSet())
		{
			if(u.getAttribute("tmplabel") != u.getAttribute("label")) 
			{
				isEnded=false;
				u.setAttribute("label", u.getAttribute("tmplabel"));
				
			}
			
		}
		return isEnded;
	}


	public DistanceFunction dist ;

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
	public final Integer getIndexWithPrediction(PartitionMap partitionMap, Node n) {

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

	public final Integer getIndexWithDispersionForKway(PartitionMap partitionMap, Node n) {
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
	public final Integer getIndexWithDispersion(PartitionMap partitionMap, Node n) {
		
		Double T=(double)n.getGraph().getNodeCount()/(double)(partitionMap.getC()*partitionMap.getK());
		
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
//			System.out.println(n.getId()+"->"+p.getId()+" \tNDISP:"+ndisp+" \tDISP:"+Dispersion.getDispersion(n, p, dist)+" \tEMB:"+cuv.size()+"\t SCORE:"+(2-ndisp));
			
//			if(Math.pow(2, val) > 1) System.out.println("GESOCRISTO");
//			else System.out.println("A MARONNNNNNNNNNNNNNNNNNNNNNNNNNNN");

			double beta=1-T;
			
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



	public abstract Double getWeight(Double partitionSize, Integer c);
	public String getHeuristicName() {
		return "Absolute Dispersion Based";
	}

}
