package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

/**
 * @author dar
 *
 */
public class BalanceBig implements SGPHeuristic {

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		if (Double.max((double) n.getDegree(), partitionMap.getDegreeAverage()) == (double)n.getDegree()) {
			return new BalancedHeuristic().getIndex(partitionMap,n);
		} else {
			return new LinearWeightedDeterministicGreedy().getIndex(partitionMap, n);
		}
	
	}


	public String getHeuristicName() {
		return "Balance Big";
	}

}
