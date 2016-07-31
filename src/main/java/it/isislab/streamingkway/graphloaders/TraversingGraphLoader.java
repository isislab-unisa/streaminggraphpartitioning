package it.isislab.streamingkway.graphloaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

	public TraversingGraphLoader(File fpIn, File fpOut, Integer k, SGPHeuristic heuristic, 
			Integer c, boolean thereIsC, GraphTraversingOrdering gto) throws IOException{
		this.heuristic = heuristic;
		this.K = k;
		this.thereIsC = thereIsC;
		if (thereIsC) {
			this.capacity = c;			
		}
		this.gto = gto;
		//file
		this.scanner = new Scanner(fpIn);
		this.printerOut = new PrintWriter(new BufferedWriter(new FileWriter(fpOut)));
	}

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
		//FIXME check out the documentation
		System.out.println(connectedComponents);
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
