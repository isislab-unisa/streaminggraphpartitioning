package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import it.isislab.streamingkway.graphloaders.GraphLoader;

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
public class Converter  {

	private static final String NODE_ID = "Node.nodeId";
	@SuppressWarnings("unused")
	private FileInputStream fpIn;
	@SuppressWarnings("unused")
	private FileOutputStream fpOut;
	private BufferedReader reader;
	private PrintWriter writer;
	private Graph gr;
	private String sep;

	public Converter(FileInputStream in, FileOutputStream out, String sep) throws IOException {
		this.fpIn = in;
		this.fpOut = out;
		this.reader = new BufferedReader(new FileReader(in.getFD()));
		this.writer = new PrintWriter(out);
		this.gr = new SingleGraph("g");
		this.sep = sep;
		gr.setStrict(false);
	}
	
	public void runConversion() throws NumberFormatException, IOException {
		String line ="";
		Integer edgeCount = 0;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			line = line.trim();
			String[] edgePoints = line.split(sep);
//			System.out.print(edgePoints[0] + " : " ) ;
//			System.out.println(edgePoints[1]);
			if (edgePoints.length < 2) continue;
			gr.addNode(edgePoints[0]);
			gr.addNode(edgePoints[1]);
			gr.addEdge((edgeCount++).toString(), edgePoints[0] , edgePoints[1]);
		}
		
		this.writer.println(gr.getNodeCount() + " " + gr.getEdgeCount());
		
		Integer nodeCount = 0;
		Iterator<Node> nIt = gr.getNodeIterator();
		ArrayList<Node> arrNodes = new ArrayList<>();
		while (nIt.hasNext()) {
			Node nx = nIt.next();
			arrNodes.add(nx);
			nx.setAttribute(NODE_ID, ++nodeCount);
		}
	
		arrNodes.stream()
			.forEach(p -> {
				writer.println(getNeigh(p));
				System.out.println("" + p.getAttribute(NODE_ID));
				writer.flush();
			});
		writer.flush();
		writer.close();
		
	}

	private String getNeigh(Node n) {
		String nodeNString = "";
		Iterator<Node> nn = n.getNeighborNodeIterator();
		while (nn.hasNext()) {
			nodeNString += " " + nn.next().getAttribute(NODE_ID);
		}
		
		return nodeNString;
	}




}
