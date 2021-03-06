package it.isislab.streamingkway.partitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.exceptions.InvalidCapacity;
import it.isislab.streamingkway.exceptions.PartitionOutOfBoundException;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;

public class SetPartitionMap implements PartitionMap {

	@SuppressWarnings("unused")
	private static final int DEGREE_DENOM = 2;
	private Map<Integer, Collection<Node>> chm;
	private Map<Integer, Integer> partitionsSize; 
	private Map<Integer,Integer> degreeMap; 
	private Integer size = 0;
	private Integer K;
	private Integer C;
	
	public SetPartitionMap(Integer k, Integer capacity) {
		this.K = k;
		chm = new ConcurrentHashMap<Integer, Collection<Node>>(k);
		partitionsSize = new ConcurrentHashMap<Integer, Integer>(k);
		degreeMap = new ConcurrentHashMap<Integer,Integer>();
		if (capacity <= 0) {
			throw new InvalidCapacity("Capacity must be greater than 0");
		}
		this.C = capacity;
		for (int i = 1; i <= k; i++) {
			chm.put(i, new HashSet<Node>(capacity/2));
		}
		for (int i = 1; i <= k; i++) {
			partitionsSize.put(i, 0);
		}
	}

	public Integer getPartitionSize(Integer ind) throws PartitionOutOfBoundException {
		checkIndex(ind);
		return chm.get(ind).size();
	}


	public Node assignToPartition(Node v, Integer ind) throws PartitionOutOfBoundException {
		checkIndex(ind);
		Collection<Node> s = chm.get(ind);
		
		if (s.size() > C) throw new PartitionOutOfBoundException("Partition " + ind + " is already full");
		
		s.add(v);
		//update size
		partitionsSize.put(ind, partitionsSize.get(ind) + 1);
		size++;
		//add to degreeMap
		if (degreeMap.containsKey(v.getDegree())) {
			degreeMap.put(v.getDegree(), degreeMap.get(v.getDegree()) +1);
		} else {
			degreeMap.put(v.getDegree(), 1);
		}
		//end update size
		return v;
	}

	public Integer getFullSize() {
		return size;
	}

	public Map<Integer, Integer> getPartitionsSize() {
		return partitionsSize;
	}

	public Integer getK() {
		return this.K;
	}
	
	public Integer getC() {
		return this.C;
	}

	private boolean checkIndex(Integer ind) throws PartitionOutOfBoundException {
		if (ind > K || ind <= 0) {
			throw new PartitionOutOfBoundException("Partition with index " + ind + " does not exists");
		}
		return true;
	}

	public Collection<Node> getPartition(Integer index) {
		return this.chm.get(index);
	}

	public List<Node> getIntersectionNodes(Node v, Integer partitionIndex) throws PartitionOutOfBoundException {
		
		checkIndex(partitionIndex);
		Iterator<Node> vNeigh = v.getNeighborNodeIterator();
		List<Node> intersection = new ArrayList<Node>(1);
		while (vNeigh.hasNext()) {
			Node u = vNeigh.next();
			if (!u.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) continue;
			if (Integer.parseInt(u.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) == (partitionIndex)) {
				intersection.add(u);
			}
		}
		return intersection;
	}
	
	public Integer getIntersectionValueParallel(Node v, Integer partitionIndex) throws PartitionOutOfBoundException {
		checkIndex(partitionIndex);
		Collection<Node> partition = this.chm.get(partitionIndex);
		Integer intersection = 0;
	
		intersection = (int) partition.parallelStream()
				.filter(p -> p.hasEdgeBetween(v))
				.count();
		
		return intersection;
	}
	
	public Map<Integer, Collection<Node>> getPartitions() {
		return this.chm;
	}

	public Integer getIntersectionValue(Node v, Integer partitionIndex) throws PartitionOutOfBoundException {
		checkIndex(partitionIndex);
		Iterator<Node> vNeigh = v.getNeighborNodeIterator();
		int intersection = 0;
		while (vNeigh.hasNext()) {
			Node u = vNeigh.next();
			if (!u.hasAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) continue;
			if (Integer.parseInt(u.getAttribute(GraphPartitionator.PARTITION_ATTRIBUTE)) == (partitionIndex)) {
				intersection ++;
			}
		}
		return intersection;
	}

	public Double getDegreeAverage() {
		if (degreeMap.isEmpty()) {
			return 0.0;
		}
		
		double ex=  degreeMap.entrySet().parallelStream()
				.mapToDouble(p -> p.getValue() * p.getKey())
				.sum();
		
		int total = degreeMap.keySet().parallelStream()
				.mapToInt(p -> p.intValue())
				.sum();
		
		return ex/total;
	}

	public Integer getTotalPartitionedNodes() {
		Integer totalNodes = partitionsSize.entrySet().parallelStream()
				.mapToInt(p -> p.getValue()	)
				.sum();
		return totalNodes;
	}

	public List<Node> getIntersectionNodesParallel(Node v, Integer partitionIndex)
			throws PartitionOutOfBoundException {
		checkIndex(partitionIndex);
		Collection<Node> partition = this.chm.get(partitionIndex);
		List<Node> gammaVintersect = Collections.synchronizedList(new ArrayList<>());

		partition.parallelStream()
			.filter(p -> p.hasEdgeBetween(v))
			.forEach(p -> gammaVintersect.add(p));
		return gammaVintersect;
	}

	@Deprecated
	public Integer getNodePartition(Node v) {
		for (Entry<Integer, Collection<Node>> part : this.chm.entrySet()) {
			if (part.getValue().isEmpty()) continue;
			
			if (part.getValue().contains(v)) {
				return part.getKey();
			}
		}
		return null;
	}
	
	public double getTrianglesValue(Node n, Integer partitionIndex) {
		int totalEdges = 0;

		List<Node> gammaNIntersect = getIntersectionNodes(n, partitionIndex);
		for (int i = 0; i < gammaNIntersect.size(); i++) {
			for (int j = i+1; j < gammaNIntersect.size(); j++) {
				if (gammaNIntersect.get(i).hasEdgeBetween(gammaNIntersect.get(j))) {
					totalEdges ++;
				}
			}
		}
		Integer N = gammaNIntersect.size();
		Integer binCoeff = N == 1 || N == 0 ? 0 : N*(N-1)>>1; //>>1 = /2
		if (binCoeff == 0) {  //hardcoded 0.0 because totalEdges must be 0 too. check it out
			return 0.0;
		}
		return (double)totalEdges/binCoeff; //safe to calculate because binCoeff != 0
	}

}
