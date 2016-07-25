package it.isislab.streamingkway.metrics;

import org.graphstream.graph.Graph;

import it.isislab.streamingkway.partitions.PartitionMap;

public interface QualityChecker {
	
	public static final Integer NUM_THREADS = 8;

	public Integer getCuttingEdgesCount(Graph gr);
	public Double getDisplacement(PartitionMap pm);
	public Double getCuttingEdgeRatio(Graph gr);
	
}
