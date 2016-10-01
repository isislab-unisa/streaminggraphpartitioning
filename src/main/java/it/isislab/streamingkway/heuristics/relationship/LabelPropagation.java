package it.isislab.streamingkway.heuristics.relationship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.partitions.PartitionMap;

public class LabelPropagation implements SGPHeuristic {

	private boolean parallel = true;
	int indexTieBrek=-1;
	
	public LabelPropagation(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		setupNodes(n.getGraph(), partitionMap);
		int max_ite=5;
		while(!runLabelPropagation(n.getGraph(),n) && (max_ite--)>0);

		int index=Integer.parseInt(n.getAttribute("label"));
		if(index==0)
		{
			index=new BalancedHeuristic(parallel).getIndex(partitionMap, n);
		}

		//System.out.println(n+" -> "+index);
		return index;
	}

	private void setupNodes(Graph g, PartitionMap map)
	{
		for(Node u : g.getNodeSet())
		{ 
			u.setAttribute("label",""+getBalancedSetupLabel(u,map));
			u.setAttribute("tmplabel",""+u.getAttribute("label"));
		}
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

			if(Integer.parseInt(u.getAttribute("label")) == -1) continue;

			HashMap<Integer,Integer> labelScores=new HashMap<Integer,Integer>();
			Iterator<Node> iteNeighbors=u.getNeighborNodeIterator();

			if(Integer.parseInt(u.getAttribute("label")) != 0) 
				labelScores.put(Integer.parseInt(u.getAttribute("label")),1);

			Node p=null;
			int max=Integer.MIN_VALUE;
			Integer maxlabel=0;
			while(iteNeighbors.hasNext())
			{
				p=iteNeighbors.next();
				Integer plabel=Integer.parseInt(p.getAttribute("label"));
				if (plabel > 0){
					labelScores.put(plabel,labelScores.get(plabel)==null?1:labelScores.get(plabel)+1);
					if(labelScores.get(plabel) > max)
					{
						max=labelScores.get(plabel);
						maxlabel=plabel;
					}
				}
			}
			u.setAttribute("tmplabel", ""+maxlabel);
		}
		for(Node u : g.getNodeSet())
		{
			if(u.getAttribute("tmplabel") != u.getAttribute("label")) 
			{
				isEnded=false;
				u.setAttribute("label", ""+u.getAttribute("tmplabel"));

			}

		}
		return isEnded;
	}


	public String getHeuristicName() {
		return "Label Propagation";
	}
}
