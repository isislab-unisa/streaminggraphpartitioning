package it.isislab.streamingkway.graphloaders;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import org.graphstream.graph.Node;
import it.isislab.streamingkway.graphpartitionator.StreamingGraphPartitionator;
import it.isislab.streamingkway.heuristics.SGPHeuristic;

public class BufferedGraphLoader extends AbstractGraphLoader {
	
	private int bufferSize;
	private PriorityQueue<Node> queue;
	private Map<String, Integer> partitionTracker;
	
	protected class DegreeComparator implements Comparator<Node> {
		public int compare(Node o1, Node o2) {
			return o2.getDegree() - o1.getDegree();
		}
	}

	public BufferedGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k, SGPHeuristic heuristic,
			Integer c, boolean thereIsC, int bufferSize) throws IOException {
		super(fpIn, fpOut, k, heuristic, c, thereIsC, null);
		this.bufferSize = bufferSize;
		this.queue = new PriorityQueue<>(bufferSize, new DegreeComparator());
		this.partitionTracker = new HashMap<String, Integer>();
	}

	public void runPartition() throws NumberFormatException, IOException {
		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;
		
		String line = "";
		readFirstLine();
		
		if (!thereIsC) {
			capacity = (int)Math.ceil((double)(nodeNumbers)/K);//+1;
		}
		//create graph
		this.graphPartitionator = new StreamingGraphPartitionator(K, heuristic, capacity);
		this.gr = graphPartitionator.getGraph();
		
		//read the whole graph
		while ((line = scanner.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("%")) {
				continue;
			}
			
			String[] nNodes = line.split(" ");
			Node v = gr.addNode(Integer.toString(nodeCount++));
			gr.addNode(v.getId());
			queue.add(v);

			for (String s : nNodes) {
				gr.addEdge(v.getId()+"-"+s, v.getId(), s);
			}
			if (queue.size() >= bufferSize) {
				partitioningNodes();
			}
		}
		if (!queue.isEmpty()) {
			partitioningNodes();
		}
		finalizePartition();
		
		printerOut.flush();
		printerOut.close();
		scanner.close();
	}

	private void finalizePartition() {
		partitionTracker.entrySet().stream()
			.sorted((p1, p2) -> Integer.parseInt(p1.getKey()) - Integer.parseInt(p2.getKey()))
			.forEach(p -> printerOut.println(p.getValue()));
	}

	private void partitioningNodes() {
		while(!queue.isEmpty()) {
			Node u = queue.remove();
			int uPartition = graphPartitionator.getPartitionNode(u);
			partitionTracker.put(u.getId(), uPartition);
		}
	}
	
}
