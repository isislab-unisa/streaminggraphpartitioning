package it.isislab.streamingkway.graphpartitionator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public interface GraphPartitionator {
	
	public final static String PARTITION_ATTRIBUTE = "node.partition";
	public static final Object STYLESHEET = "node { fill-mode: dyn-plain; fill-color: black, yellow, red, green,blue; "+
	"text-mode: normal; text-color: black; text-size: 40 ; text-style: bold;  text-background-mode: plain; text-background-color: white; size : 30px; }";

	public Integer addNode(Node v, Node[] gammaV);
	public Graph getGraph();
	public Integer getPartitionNode(Node v);
	public Integer getTotalPartitionedNodes();
	public PartitionMap getPartitionMap();
}
