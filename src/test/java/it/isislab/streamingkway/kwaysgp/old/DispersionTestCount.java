package it.isislab.streamingkway.kwaysgp.old;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Test;

import it.isislab.streamingkway.heuristics.relationship.distance.Dispersion;
import it.isislab.streamingkway.heuristics.relationship.distance.DistanceFunction;
import it.isislab.streamingkway.heuristics.relationship.distance.SimpleDistanceFunction;

public class DispersionTestCount {

	
	private int nodeNumbers;
	private int edgeNumbers;

	@Test
	public void test() throws IdAlreadyInUseException, ElementNotFoundException, EdgeRejectedException, IOException {
		BufferedReader scanner = new BufferedReader(new InputStreamReader(new FileInputStream("resources/paperdispersion.graph")));
		Graph gr = new SingleGraph("aaa");
		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		String line = "";
		while ((line = scanner .readLine()) != null
				&&	line.length() != 0) {
			//String firstLine = scanner.nextLine().trim();
			line = line.trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			} else {
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
		gr = new SingleGraph("grafo");
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
		
		ArrayList<Node> nodes = new ArrayList(gr.getNodeSet());
		for (Node n : nodes) {
			System.err.println("Nodo : " + n.getId());
			Iterator<Node> nit = n.getNeighborNodeIterator();
			while (nit.hasNext()) {
				Node v = nit.next();
				System.out.print("Dispersione : " + Dispersion.getDispersion(v, n, new SimpleDistanceFunction()));
				System.out.println(" per nodo : " + v.getId());
				System.out.println("Emb: " + Dispersion.cuvCalculator(v, n).size());
			}
		}
	}

}
