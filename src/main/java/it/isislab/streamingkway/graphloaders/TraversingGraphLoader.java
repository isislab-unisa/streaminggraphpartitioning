package it.isislab.streamingkway.graphloaders;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphloaders.graphtraversingordering.BFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.DFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
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
public class TraversingGraphLoader extends AbstractGraphLoader {


	public TraversingGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k, SGPHeuristic heuristic,
			Integer c, boolean thereIsC, GraphTraversingOrdering gto) throws IOException {
		super(fpIn, fpOut, k, heuristic, c, thereIsC, gto);
	}



	/**
	 * Performs the partitioning of the graph given by the {@link FileInputStream} in the constructor.
	 * It first tries to read the number of nodes and edges that the graph should contains, then
	 * loads the graph from the stream.
	 * After that, visits the nodes according to the given {@link GraphTraversingOrdering} and assigns them to
	 * the respective partitions using the given {@link SGPHeuristic}.
	 * Whenever it assigns a node to a partition, it write the partition index in the {@link FileOutputStream}.
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public void runPartition() throws NumberFormatException, IOException {

		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		String line = "";
		readFirstLine();
		
		if (!thereIsC) {
			capacity = nodeNumbers / K + 1;
		}
		//graph
		this.graphPartitionator = new StramingGraphPartitionator(K, heuristic, capacity);
		this.gr = graphPartitionator.getGraph();
		//read the whole graph from file
		while((line = scanner.readLine()) != null &&
				line.length() != 0) {
			line = line.trim();
			//String line = scanner.nextLine().trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			}
			String[] nNodes = line.split(" ");
			Node v = gr.addNode(Integer.toString(nodeCount++));

			for (String s : nNodes) {
				gr.addEdge(v.getId()+"-"+s, v.getId(), s);
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

}
