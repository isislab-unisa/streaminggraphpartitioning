package it.isislab.streamingkway.graphloaders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.BFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.DFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.graphpartitionator.StramingGraphPartitionator;
import it.isislab.streamingkway.heuristics.SGPHeuristic;

/**
 *	A graph loader that performs the partitioning of a graph given in input according to the traversing
 * ordering given by the user.
 * It can visit the graph using the following graph traversing ordering:
 * <p><ul>
 * 	<li>BFS- breadth first search: select a random node from each connected component of the graph, then
 * 		visit it using the BFS;
 * 	<li>DFS- depth first search: select a random node from each connected component of the graph, then 
 * 		visit it using the DFS;
 *  <li>RANDOM: visit every node following a random permutation of them.
 * </ul></p>
 * This {@link GraphLoader} first load the full graph, then visit its nodes and assign them to the partitions. 
 * 
 * @author Dario Di Pasquale
 *
 */
public class TraversingGraphLoader implements GraphLoader {

	private GraphPartitionator graphPartitionator;
	private SGPHeuristic heuristic;
	private Integer K;
	private Scanner scanner;
	private PrintWriter printerOut;
	private Graph gr;
	private int nodeNumbers;
	private int edgeNumbers;
	private Integer capacity;
	private boolean thereIsC;
	private GraphTraversingOrdering gto;

	/**
	 * Creates a traversing graph loader according to the given parameters.
	 * @param fpIn the {@link FileInputStream} in which read the graph. It can be associated to a {@link File} or a {@link Socket}.
	 * @param fpOut the {@link FileOutputStream} on which write the result of the partitioning. It can be associated to a {@link File} or a {@link Socket}.
	 * @param k the number of partitions in which the graph should be partitioned.
	 * @param heuristic the {@link SGPHeuristic} used for partitioning the graph.
	 * @param c the capacity of every partition.
	 * @param thereIsC a boolean value that indicates if the capacity is defined by the user of should be evaluate according to the graph size. If this value is false, c will be (n/k)+1 where n is the count of the nodes and k is the count of partitions.
	 * @throws IOException if there is an error opening the input or output streams throw {@link IOException}
	 */
	public TraversingGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k, 
			SGPHeuristic heuristic, Integer c, boolean thereIsC, GraphTraversingOrdering gto) throws IOException{
		this.heuristic = heuristic;
		this.K = k;
		this.thereIsC = thereIsC;
		if (thereIsC) {
			this.capacity = c;			
		}
		this.gto = gto;
		//file
		this.scanner = new Scanner(new BufferedInputStream(fpIn));
		this.printerOut = new PrintWriter(new BufferedOutputStream(fpOut));
	}
	/**
	 * Performs the partitioning of the graph given by the {@link FileInputStream} in the constructor.
	 * It first tries to read the number of nodes and edges that the graph should contains, then
	 * loads the graph from the stream.
	 * After that, visits the nodes according to the given {@link GraphTraversingOrdering} and assigns them to
	 * the respective partitions using the given {@link SGPHeuristic}.
	 * Whenever it assigns a node to a partition, it write the partition index in the {@link FileOutputStream}.
	 */
	public void run() {

		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		while (scanner.hasNextLine()) {
			String firstLine = scanner.nextLine().trim();
			if (firstLine.startsWith("%")) { //it is a comment
				continue;
			} else {
				StringTokenizer strTok = new StringTokenizer(firstLine, " ");
				//read the number of nodes
				if (strTok.hasMoreTokens()) {
					String token = strTok.nextToken();
					nodeNumbers = Integer.parseInt(token);
				}
				//read the number of edges
				if (strTok.hasMoreTokens()) {
					String token = strTok.nextToken();
					edgeNumbers = Integer.parseInt(token);
				}
				if (!thereIsC) {
					capacity = nodeNumbers / K + 1;
				}
				break;
			}
		}
		if (!thereIsC) {
			capacity = nodeNumbers / K + 1;
		}
		//graph
		this.graphPartitionator = new StramingGraphPartitionator(K, heuristic, capacity);
		this.gr = graphPartitionator.getGraph();
		//read the whole graph from file
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			}
			if (line.equals("") || line.equals(" ") || line.equals('\n')) { //empty
				continue;
			}
			StringTokenizer strTok = new StringTokenizer(line, " ");
			Node v = gr.addNode(Integer.toString(nodeCount++));
			gr.addNode(v.getId());

			while (strTok.hasMoreTokens()) {
				String uId = (String) strTok.nextElement();
				gr.addEdge(v.getId()+uId, v.getId(),uId);
			}
		}
		//scanning the graph
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(gr);
		cc.compute();
		cc.setCountAttribute(GraphLoader.CONNECTED_COMPONENT_ATTR);
		int connectedComponents = cc.getConnectedComponentsCount();
		if (connectedComponents > 1 &&
				(gto.getClass().equals(BFSTraversing.class) || gto.getClass().equals(DFSTraversing.class))) { //there are at least 2 connected components
			//the algorithm requires to visit them and are not enabled to do
			List<Integer> connComps = new ArrayList<>(connectedComponents);
			while (connComps.size() < connectedComponents) {
				Node vRand = Toolkit.randomNode(gr);
				Integer ccIndex = vRand.getAttribute(GraphLoader.CONNECTED_COMPONENT_ATTR, Integer.class);
				if (!connComps.contains(ccIndex)) {
					connComps.add(ccIndex);
					Iterator<Node> traversingGraph = gto.getNodesOrdering(gr, vRand);
					while (traversingGraph.hasNext()) {
						Integer part = graphPartitionator.getPartitionNode(traversingGraph.next());
						printerOut.println(part);
					}
				}
			}
		} else {
			//choose a random node
			Node vRand = Toolkit.randomNode(gr);
			Iterator<Node> traversingGraph = gto.getNodesOrdering(gr, vRand);
			while (traversingGraph.hasNext()) {
				Integer part = graphPartitionator.getPartitionNode(traversingGraph.next());
				printerOut.println(part);
			}			
		}
		printerOut.flush();
		printerOut.close();
		scanner.close();
	}



	public GraphPartitionator getGraphPartitionator() {
		return graphPartitionator;
	}

	public int getNodeNumbers() {
		return nodeNumbers;
	}

	public int getEdgeNumbers() {
		return edgeNumbers;
	}




}
