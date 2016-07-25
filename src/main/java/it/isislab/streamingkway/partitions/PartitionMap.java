package it.isislab.streamingkway.partitions;

import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.exceptions.PartitionOutOfBoundException;

public interface PartitionMap {

	public Integer getPartitionSize(Integer ind) throws PartitionOutOfBoundException;
	public Node assignToPartition(Node v, Integer ind) throws PartitionOutOfBoundException;
	public Integer getFullSize();
	public Map<Integer, Integer> getPartitionsSize();
	public Integer getK();
	public Integer getC();
	public Set<Node> getPartition(Integer index);
	public Set<Node> getIntersectionNodes(Node v, Integer partitionIndex) throws PartitionOutOfBoundException;
	public Map<Integer, Set<Node>> getPartitions();
	public Integer getIntersectionValueParallel(Node v, Integer partitionIndex) throws PartitionOutOfBoundException;
	public Integer getIntersectionValue(Node v, Integer partitionIndex) throws PartitionOutOfBoundException;
	public Double getDegreeAverage();
	public Integer getTotalPartitionedNodes();
}
