package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import it.isislab.streamingkway.graphloaders.AbstractGraphLoader;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
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
public class OrdinatorGraphLoader extends AbstractGraphLoader {

	ArrayList<Node> nodesTrav = new ArrayList<Node>();
	Map<Node, Integer> mapNode = new HashMap<>();

	public OrdinatorGraphLoader(FileInputStream fpIn, FileOutputStream fpOut,GraphTraversingOrdering gto) throws IOException {
		super(fpIn, fpOut, 0, null, 0, false, gto);
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
		nodesTrav.add(null);
		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		String line = "";
		while ((line = scanner.readLine()) != null
				&&	line.length() != 0) {
			//String firstLine = scanner.nextLine().trim();
			line = line.trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			} else {
				printerOut.print(line);
				StringTokenizer strTok = new StringTokenizer(line, " ");
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
				break;
			}
		}
		//graph
		this.gr = new SingleGraph("grafo");
		gr.setStrict(false);

		//read the whole graph from file
		while((line = scanner.readLine()) != null) {
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
		System.out.println(nodeCount);
		//scanning the graph
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(gr);
		cc.compute();
		cc.setCountAttribute(GraphLoader.CONNECTED_COMPONENT_ATTR);
		nodeCount = 1;
		int connectedComponents = cc.getConnectedComponentsCount();
		if (connectedComponents > 1) {
			//the algorithm requires to visit them and are not enabled to do
			List<Integer> connComps = new ArrayList<>(connectedComponents);
			while (connComps.size() < connectedComponents) {
				Node vRand = Toolkit.randomNode(gr);
				Integer ccIndex = vRand.getAttribute(GraphLoader.CONNECTED_COMPONENT_ATTR, Integer.class);
				if (!connComps.contains(ccIndex)) {
					connComps.add(ccIndex);
					if (vRand.getDegree() == 0) {
						populateStructs(vRand, nodeCount++);
					} else {
						Iterator<Node> traversingGraph = gto.getNodesOrdering(gr, vRand);
						while (traversingGraph.hasNext()) {
							populateStructs(traversingGraph.next(), nodeCount++);
						}						
					}
				}
			}
		} else {
			Node vRand = Toolkit.randomNode(gr);
			Iterator<Node> traversingGraph = gto.getNodesOrdering(gr, vRand);
			while (traversingGraph.hasNext()) {
				populateStructs(traversingGraph.next(),nodeCount++);
			}		
		}
		writeFile();
		printerOut.flush();
		printerOut.close();
		scanner.close();
	}



	private void populateStructs(Node next, int nIndex) {
		nodesTrav.add(nIndex,next);
		mapNode.put(next, nIndex);
	}
	private void writeFile() {
		nodesTrav.remove(0);
		for (Node node : nodesTrav) {
			String s = "";
			if (node.getDegree() == 0) {
				s = " ";
			} else {
				Iterator<Node> nNeigh = node.getNeighborNodeIterator();
				while(nNeigh.hasNext()) {
					Node u = nNeigh.next();
					s += " " + mapNode.get(u);
				}				
			}
			printerOut.print('\n' + s);
		}
	}




}
