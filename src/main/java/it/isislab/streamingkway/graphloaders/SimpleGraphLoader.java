package it.isislab.streamingkway.graphloaders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.graphpartitionator.StramingGraphPartitionator;
import it.isislab.streamingkway.heuristics.SGPHeuristic;

/**
 * @author Dario Di Pasquale
 *
 *	A simple graph loader that performs the partitioning of a graph given in input according to the traversing
 * ordering given by the file.
 *  It does not works well for some heuristics because the information lack concerns the node location but
 *  it assigns a node at a partition as well as it is read and does not need immediately the full graph.
 *
 */
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

	/**
	 * Creates a simple graph loader according to the given parameters.
	 * @param fpIn the {@link FileInputStream} in which read the graph. It can be associated to a {@link File} or a {@link Socket}.
	 * @param fpOut the {@link FileOutputStream} on which write the result of the partitioning. It can be associated to a {@link File} or a {@link Socket}.
	 * @param k the number of partitions in which the graph should be partitioned.
	 * @param heuristic the {@link SGPHeuristic} used for partitioning the graph.
	 * @param c the capacity of every partition.
	 * @param thereIsC a boolean value that indicates if the capacity is defined by the user of should be evaluate according to the graph size. If this value is false, c will be (n/k)+1 where n is the count of the nodes and k is the count of partitions.
	 * @throws IOException if there is an error opening the input or output streams throw {@link IOException}
	 */
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

	/**
	 * Performs the partitioning of the graph given by the {@link FileInputStream} in the constructor.
	 * It first tries to read the number of nodes and edges that the graph should contains, then starts
	 * loading and partitioning the graph according to the {@link SGPHeuristic} given in the constructor.
	 * For every node that load in the stream, it retrieve the partition in which it should be and then 
	 * assign the node to the partition and write this information on the {@link FileOutputStream} given to
	 * the constructor.
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
		while(scanner.hasNextLine()) {
			
			String line = scanner.nextLine().trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			}
			if (line.equals("") || line.equals(" ") || line.equals('\n')) { //empty
				continue;
			}
			String[] nNodes = line.split(" ");
			Node v = gr.addNode(Integer.toString(nodeCount++));
			gr.addNode(v.getId());

			for (String s : nNodes) {
				gr.addEdge(v.getId()+"-"+s, v.getId(), s);
			}
			int uPartition = graphPartitionator.getPartitionNode(v);
			printerOut.println(uPartition);
		}

		printerOut.flush();
		printerOut.close();
		scanner.close();
	}

	public GraphPartitionator getGraphPartitionator() {
		return graphPartitionator;
	}

	public int getNodeNumbers() {
		return nodeNumbers == gr.getNodeCount() ? nodeNumbers : -1;
	}
	

	public int getEdgeNumbers() {
		return edgeNumbers == gr.getEdgeCount() ? edgeNumbers : -1;
	}




}
