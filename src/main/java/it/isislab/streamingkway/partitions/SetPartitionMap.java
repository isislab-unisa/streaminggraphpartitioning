package it.isislab.streamingkway.partitions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;

import org.graphstream.graph.Node;

import it.isislab.streamingkway.exceptions.PartitionOutOfBoundException;

public class SetPartitionMap implements PartitionMap {

	private ConcurrentHashMap<Integer, Set<Node>> chm;
	private ConcurrentHashMap<Integer, Integer> partitionsSize; //TODO to remove?
	private ConcurrentHashMap<Integer,Integer> degreeMap; 
	private Integer size = 0;
	private Integer K;
	private Integer C;

	public SetPartitionMap(Integer k, Integer capacity) {
		this.K = k;
		chm = new ConcurrentHashMap<Integer, Set<Node>>(k);
		partitionsSize = new ConcurrentHashMap<Integer, Integer>(k);
		degreeMap = new ConcurrentHashMap<Integer,Integer>();
		int capacityInit = 0;
		if (capacity != Integer.MAX_VALUE) {
			capacityInit = capacity/2;
			this.C = capacity;
		} else {
			this.C = Integer.MAX_VALUE;
		}
		for (int i = 1; i <= k; i++) {
			chm.put(i, new HashSet<Node>(capacityInit));
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
		Set<Node> s = chm.get(ind);
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

	public Set<Node> getPartition(Integer index) {
		return this.chm.get(index);
	}

	public Set<Node> getIntersectionNodes(Node v, Integer partitionIndex) throws PartitionOutOfBoundException {
		checkIndex(partitionIndex);
		Iterator<Node> vNeigh = v.getNeighborNodeIterator();
		Set<Node> partition = this.chm.get(partitionIndex);
		Set<Node> intersection = new HashSet<Node>(1);
		while (vNeigh.hasNext()) {
			Node u = vNeigh.next();
			if (partition.contains(u)) {
				intersection.add(u);
			}
		}
		return intersection;
	}
	
	public Integer getIntersectionValueParallel(Node v, Integer partitionIndex) throws PartitionOutOfBoundException {
		checkIndex(partitionIndex);
		Set<Node> partition = this.chm.get(partitionIndex);
		Integer intersection = 0;
		
		intersection = partition.parallelStream().mapToInt(new ToIntFunction<Node>() {

			public int applyAsInt(Node value) {
				return value.hasEdgeBetween(v.getId())? 1 : 0;
			}
			
		}).sum();
		
		return intersection;
	}
	
	public Map<Integer, Set<Node>> getPartitions() {
		return this.chm;
	}

	public Integer getIntersectionValue(Node v, Integer partitionIndex) throws PartitionOutOfBoundException {
		checkIndex(partitionIndex);
		Set<Node> partition = this.chm.get(partitionIndex);
		Integer intersection = 0;
		
		partition.stream().mapToInt(new ToIntFunction<Node>() {

			public int applyAsInt(Node value) {
				return value.hasEdgeBetween(v.getId())? 1 : 0;
			}
			
		}).sum();
		
		return intersection;
	}

	public Double getDegreeAverage() {
		if (degreeMap.isEmpty()) {
			return 0.0;
		}
		
		double ex=  degreeMap.entrySet().parallelStream().mapToDouble(
			p -> p.getValue() * p.getKey()
		).sum();
		
		int total = degreeMap.keySet().parallelStream().mapToInt(
			p -> p.intValue()
		).sum();
		
		return ex/total;
	}

	public Integer getTotalPartitionedNodes() {
		Integer totalNodes = partitionsSize.entrySet().parallelStream().mapToInt(
				p -> p.getValue()
		).sum();
		return totalNodes;
	}
	

}
