package it.isislab.streamingkway.heuristics;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

/**
 * @author dar
 *
 */
public class BalanceBig implements SGPHeuristic {
	
	private boolean parallel;
	
	public BalanceBig(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		if (Double.max((double) n.getDegree(), partitionMap.getDegreeAverage()) == (double)n.getDegree()) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap,n);
		} else {
			return new LinearWeightedDeterministicGreedy(parallel).getIndex(partitionMap, n);
		}
	
	}


	public String getHeuristicName() {
		return "Balance Big" + (parallel ? " Parallel" : "");
	}

}
