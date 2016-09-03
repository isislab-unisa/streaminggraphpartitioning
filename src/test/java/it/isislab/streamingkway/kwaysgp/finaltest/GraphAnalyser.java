package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import au.com.bytecode.opencsv.CSVWriter;
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
public class GraphAnalyser {

	private CSVWriter writer;
	private int nodeNumbers;
	private int edgeNumbers;
	private BufferedReader scanner;
	private Graph gr;
	private Logger log;


	public GraphAnalyser(FileInputStream fpIn, CSVWriter wr, Logger log) throws IOException {
		writer = wr;
		scanner = new BufferedReader(new FileReader(fpIn.getFD()));
		this.log = log;
	}




	public void runLoad(String grName) throws NumberFormatException, IOException, InterruptedException {
		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		String line = "";
		log.info("Reading the graph");
		while ((line = scanner.readLine()) != null) {
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
		
		log.info("Counting nodes and edges");
		Integer nn = gr.getNodeCount();
		Integer en = gr.getEdgeCount();
		log.info("Counting connected components");
		ConnectedComponents cc = new ConnectedComponents(gr);
		cc.init(gr);
		cc.compute();
		Integer connectedComps = cc.getConnectedComponentsCount();
		log.info("Counting degrees");
		Double averageDegree = Toolkit.averageDegree(gr);
		ArrayList<Node> degreeMap = Toolkit.degreeMap(gr); //FIST AND LAST
		Integer maxDegree = degreeMap.get(0).getDegree();
		Integer minDegree = degreeMap.get(degreeMap.size() -1).getDegree();
		log.info("Counting density");
		Double density = Toolkit.density(gr);
		log.info("Cluster coeff density");
		Double avgClusterCoeff = Toolkit.averageClusteringCoefficient(gr);
		gr = null;
		System.gc();
		String[] details = {
			grName,
			Integer.toString(nodeNumbers == nn ? nodeNumbers : -1),
			Integer.toString(edgeNumbers == en ? edgeNumbers : -1),
			avgClusterCoeff.toString(),
			density.toString(),
			maxDegree.toString(),
			minDegree.toString(),
			averageDegree.toString(),
			connectedComps.toString()
		};
		log.info("Writing");
		writer.writeNext(details);
		writer.flush();
		
	}




	
	}
