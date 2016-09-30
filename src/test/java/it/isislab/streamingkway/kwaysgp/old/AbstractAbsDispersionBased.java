package it.isislab.streamingkway.kwaysgp.old;

import java.util.ArrayList;
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
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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


public abstract class AbstractAbsDispersionBased  implements SGPHeuristic, WeightedHeuristic{

	protected boolean parallel;
	public AbstractAbsDispersionBased(boolean parallel) {
		this.parallel = parallel;
	}
	public final Integer getIndex(PartitionMap partitionMap, Node n) {

		return getIndexWithDispersionWithPredictionNeighbors(partitionMap, n);
	}	
	int indexTieBrek=-1;
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

	public final Integer getIndexWithDispersionWithPredictionNeighbors(PartitionMap partitionMap, Node n) {

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
	int count=0;

	public final Integer getIndexWithDispersion(PartitionMap partitionMap, Node n) {


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



	public abstract Double getWeight(Double partitionSize, Integer c);
	public String getHeuristicName() {
		return "Absolute Dispersion Based";
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
	public final Integer getIndexWithSTC(PartitionMap partitionMap, Node n) {
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

}
