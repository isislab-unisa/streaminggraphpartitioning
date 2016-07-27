package it.isislab.streamingkway.heuristics;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.partitions.PartitionMap;

public abstract class AbstractTriangles implements SGPHeuristic,WeightedHeuristic {

	public Integer getIndex(Graph g, PartitionMap partitionMap, Node n) {
		Integer c = partitionMap.getC();
		Integer index = -1;
		Map<Integer,Collection<Node>> parts = partitionMap.getPartitions();
	
		
		Double max = Double.NEGATIVE_INFINITY;
		Iterator<Entry<Integer, Collection<Node>>> partsIt = parts.entrySet().iterator();
		while (partsIt.hasNext()) {
			Entry<Integer, Collection<Node>> t = partsIt.next();
			Integer partitionIndex = t.getKey();
			Collection<Node> partitionNodes = t.getValue();
			if (partitionNodes.size() >= c) { //partition sated
				continue;
			}
			
			int totalEdges = 0;
			List<Node> gammaNIntersect = partitionMap.getIntersectionNodesParallel(n, partitionIndex);
			for (int i = 0; i < gammaNIntersect.size(); i++) {
				for (int j = 1; j < gammaNIntersect.size(); j++) {
					if (gammaNIntersect.get(i).hasEdgeBetween(gammaNIntersect.get(j))) {
						totalEdges ++;
					}
				}
			}

			//calculate score
			Double totalScore = 0.0;
			Integer N = partitionMap.getIntersectionValueParallel(n, partitionIndex);
			double weight = getWeight((double)N, c);
			Integer binCoeff = N == 1 || N == 0 ? 0 : N*(N-1)/2;
			if (binCoeff == 0) {  //hardcoded 0.0 because totalEdges must be 0 too. check it out
				if (Double.max(max, 0.0) == 0.0) {  
					max = 0.0;
					index = partitionIndex;
				}
				continue;
			}
			totalScore = (double)totalEdges/binCoeff * weight; //safe to calculate because binCoeff != 0
			
			if (Double.max(max, totalScore) == totalScore) {
				max = totalScore;
				index = partitionIndex;
			} else if (Double.compare(max, totalScore) == 0) { //tie break
				int competitorSize = parts.get(index).size();
				if (competitorSize > partitionNodes.size()) {
					max = totalScore;
					index = partitionIndex;
				} else if (competitorSize == partitionNodes.size()) {
					boolean ivsc = new Random().nextBoolean();
					if (ivsc) {
						max = totalScore;
						index = partitionIndex;
					}
				}
				
			}
		}

		return index;
	}
	
	public abstract Double getWeight(Double intersectNumber, Integer c);
	public abstract String getHeuristicName();

}
