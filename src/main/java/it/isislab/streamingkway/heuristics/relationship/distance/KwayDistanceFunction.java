package it.isislab.streamingkway.heuristics.relationship.distance;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;

public class KwayDistanceFunction implements DistanceFunction {

	
	public Integer getDistance(Node s, Node t, Node u, Node v) {
		
		SimpleDistanceFunction dist=new SimpleDistanceFunction();
		
		if(!v.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) return DIST_POS;
		
		Integer i=Integer.parseInt(v.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE));
		Integer j=Integer.parseInt(s.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)?s.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE):"0");
		Integer k=Integer.parseInt(t.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)?t.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE):"0");
		
		if(i==j && j==k) return DIST_NEG;
		
		if(j!=i || k!=i) return DIST_POS;
		
		return dist.getDistance(s, t, u, v);
	}

	@Override
	public Integer getDistance(Node s, Node t, Node u, Node v, int emb) {
		// TODO Auto-generated method stub
		return null;
	}

}
