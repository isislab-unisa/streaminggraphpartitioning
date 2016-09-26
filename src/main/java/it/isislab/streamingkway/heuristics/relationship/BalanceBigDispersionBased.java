package it.isislab.streamingkway.heuristics.relationship;

import org.graphstream.graph.Node;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.partitions.PartitionMap;

public class BalanceBigDispersionBased implements SGPHeuristic {
	private boolean parallel;

	public BalanceBigDispersionBased(boolean parallel) {
		this.parallel = parallel;
	}

	public Integer getIndex(PartitionMap partitionMap, Node n) {
		if (Double.max((double) n.getDegree(), partitionMap.getDegreeAverage()) == (double)n.getDegree()) {
			return new BalancedHeuristic(parallel).getIndex(partitionMap,n);
		} else {
			return new LinearAbsWeightedDispersionBased(parallel).getIndex(partitionMap, n);
		}

	}


	public String getHeuristicName() {
		return "Balance Big Dispersion Based" + (parallel ? " Parallel" : "");
	}
}
