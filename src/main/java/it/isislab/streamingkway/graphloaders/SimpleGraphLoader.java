package it.isislab.streamingkway.graphloaders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.graphpartitionator.StramingGraphPartitionator;
import it.isislab.streamingkway.heuristics.SGPHeuristic;

public class SimpleGraphLoader implements GraphLoader {

	private GraphPartitionator graphPartitionator;
	private SGPHeuristic heuristic;
	private Integer K;
	private Integer capacity;
	private Scanner scanner;
	private PrintWriter printerOut;
	private Graph gr;
	private int nodeNumbers;
	private int edgeNumbers;
	private boolean thereIsC;


	public SimpleGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k, SGPHeuristic heuristic, Integer c, boolean thereIsC) throws IOException{
		this.heuristic = heuristic;
		this.K = k;
		this.thereIsC = thereIsC;
		if (thereIsC) {
			this.capacity = c;			
		}
		//file
		this.scanner = new Scanner(new BufferedInputStream(fpIn));
		this.printerOut = new PrintWriter(new BufferedOutputStream(fpOut));
	}

	public SimpleGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k2, Integer heuristicNumber,
			Integer c, boolean thereIsC2) {
		// TODO Auto-generated constructor stub
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
				break;
			}
		}
		if (!thereIsC) {
			capacity = nodeNumbers / K + 1;
		}
		//create graph
		this.graphPartitionator = new StramingGraphPartitionator(K, heuristic, capacity);
		this.gr = graphPartitionator.getGraph();
		//read the whole graph
		NodeFactory<? extends Node> nf = gr.nodeFactory();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.startsWith("%")) { //it is a comment
				continue;			
			}
			//tokeninzing first line and extracting the node and all its edges
			StringTokenizer strTok = new StringTokenizer(line, " ");

			Node v = nf.newInstance(Integer.toString(nodeCount++), gr);
			ArrayList<Node> gammaVAL = new ArrayList<Node>();
			while (strTok.hasMoreTokens()) {
				String uId = (String) strTok.nextElement();
				gammaVAL.add(nf.newInstance(uId,gr));
			}
			Node[] gammaV = new Node[gammaVAL.size()];
			gammaVAL.toArray(gammaV);
			Integer uPartition = graphPartitionator.addNode(v, gammaV);
			printerOut.println(uPartition); //writes the partition number
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
