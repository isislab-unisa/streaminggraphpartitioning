package it.isislab.streamingkway.heuristics.relationship.distance;


import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.partitions.PartitionMap;

public class Kway2DistanceFunction implements DistanceFunction {

	PartitionMap map;
	public Kway2DistanceFunction(PartitionMap map)
	{
		this.map=map;
	}
	SimpleDistanceFunction dist=new SimpleDistanceFunction();
	public Integer getDistance(Node s, Node t, Node u, Node v,int emb) {


		if(!v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) return 1;

		Integer i=Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
		Integer j=Integer.parseInt(s.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)?s.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE):"-1");
		Integer k=Integer.parseInt(t.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)?t.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE):"-1");
		
		if(i==j && k==i) return 1;
		if(j!=-1 || k!=-1 ) return 0;
	
		return -1;
	}

	@Override
	public Integer getDistance(Node s, Node t, Node u, Node v) {
		// TODO Auto-generated method stub
		return null;
	}

}
