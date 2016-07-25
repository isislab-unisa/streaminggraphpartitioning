package it.isislab.streamingkway.graphpartitionator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;
import it.isislab.streamingkway.partitions.SetPartitionMap;

public class StramingGraphPartitionator implements GraphPartitionator {

	private Graph graph;
	@SuppressWarnings("unused")
	private Integer k;
	private SGPHeuristic heuristic;
	private PartitionMap partitionMap;


	public StramingGraphPartitionator(Integer k, SGPHeuristic heuristic, Integer capacity) {
		String graphId = Integer.toString(new java.util.Random().nextInt(Integer.MAX_VALUE));
		this.graph = createSingleGraph(graphId);
		this.heuristic = heuristic;
		this.k = k;
		this.partitionMap = new SetPartitionMap(k,capacity);
	}

	public Integer addNode(Node v, Node[] gammaV) {
		graph.addNode(v.getId());
		int l = gammaV.length;
		for (int i = 0; i < l; i++) {
			Node u = gammaV[i];
			graph.addEdge(v.getId() + u.getId(), v.getId(), u.getId());
		}
		Integer index = heuristic.getIndex(graph,partitionMap, v);
		partitionMap.assignToPartition(v, index);
		Node addedV = graph.getNode(v.getId());
		addedV.addAttribute(GraphPartitionator.PARTITION_ATTRIBUTE, Integer.toString(index));
		Double vColor = 1.0/index;
		addedV.addAttribute("ui.color", vColor);
		return index;
	}

	public Integer getPartitionNode(Node v) {
		Integer index = heuristic.getIndex(graph,partitionMap, v);
		partitionMap.assignToPartition(v, index);
		Node addedV = graph.getNode(v.getId());
		addedV.addAttribute(GraphPartitionator.PARTITION_ATTRIBUTE, Integer.toString(index));
		Double vColor = 1.0/index;
		addedV.addAttribute("ui.color", vColor);
		return index;
	}

	public Graph getGraph() {
		return this.graph;
	}

	private Graph createSingleGraph(String graphId) {
		Graph gr = new SingleGraph(graphId);
		gr.setStrict(false);
		gr.addAttribute("ui.stylesheet", GraphPartitionator.STYLESHEET);
		return gr;
	}

	public Integer getTotalPartitionedNodes() {
		return this.partitionMap.getTotalPartitionedNodes();
	}

	public PartitionMap getPartitionMap() {
		return this.partitionMap;
	}

	
}
