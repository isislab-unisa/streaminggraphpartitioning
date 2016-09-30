package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import it.isislab.streamingkway.graphloaders.AbstractGraphLoader;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;


public class RandomOrdinator extends AbstractGraphLoader {

	ArrayList<Node> nodesTrav = new ArrayList<Node>();
	Map<Node, Integer> mapNode = new HashMap<>();

	public RandomOrdinator(FileInputStream fpIn, FileOutputStream fpOut,GraphTraversingOrdering gto) throws IOException {
		super(fpIn, fpOut, 0, null, 0, false, gto);
	}


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
				printerOut.println(line);
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
		//scanning the graph
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(gr);
		cc.compute();
		cc.setCountAttribute(GraphLoader.CONNECTED_COMPONENT_ATTR);
		nodeCount = 1;
		//Node vRand = Toolkit.randomNode(gr);
		//Iterator<Node> traversingGraph = gto.getNodesOrdering(gr, vRand);
		java.util.Collection<Node> nodesSet = gr.getNodeSet();
		ArrayList<Node> nodes = new ArrayList(nodesSet);
		Collections.shuffle(nodes, new Random(System.currentTimeMillis()));
		//			
		//			while (traversingGraph.hasNext()) {
		//				populateStructs(traversingGraph.next(),nodeCount++);
		//			}
		HashMap<Integer, Integer> indexMap = new HashMap<>();
		int id = 1;
		for (Node n : nodes) {
			indexMap.put(Integer.parseInt(n.getId()) ,id++);
		}
		//printerOut.println(gr.getNodeCount() + " " + gr.getEdgeCount());
		for (Node n : nodes) {
			Iterator<Node> nn = n.getNeighborNodeIterator();
			while (nn.hasNext()) {
				Node v = nn.next();
				printerOut.print(indexMap.get(Integer.parseInt(v.getId())) + " ");
			}
			printerOut.println();
		}

		//writeFile();
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
