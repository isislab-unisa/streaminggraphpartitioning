package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

/**
 * @author dar
 *
 */
public class BalanceBig implements SGPHeuristic {

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		if (Double.max((double) n.getDegree(), partitionMap.getDegreeAverage()) == (double)n.getDegree()) {
			return new BalancedHeuristic().getIndex(g,partitionMap, n);
		} else {
			return new LinearWeightedDeterministicGreedy().getIndex(g, partitionMap, n);
		}
	
	}


	public String getHeuristicName() {
		return "Balance Big";
	}

}
