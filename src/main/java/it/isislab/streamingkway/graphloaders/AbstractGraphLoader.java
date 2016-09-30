package it.isislab.streamingkway.graphloaders;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import org.graphstream.graph.Graph;

import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
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
public abstract class AbstractGraphLoader implements GraphLoader {

	protected GraphPartitionator graphPartitionator;
	protected SGPHeuristic heuristic;
	protected Integer K;
	protected BufferedReader scanner;
	protected PrintWriter printerOut;
	protected Graph gr;
	protected int nodeNumbers;
	protected int edgeNumbers;
	protected Integer capacity;
	protected boolean thereIsC;
	protected GraphTraversingOrdering gto;
	protected Long partTime;

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
	public AbstractGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k, 
			SGPHeuristic heuristic, Integer c, boolean thereIsC, GraphTraversingOrdering gto) throws IOException{
		this.heuristic = heuristic;
		this.K = k;
		this.partTime = 0L;
		this.thereIsC = thereIsC;
		if (thereIsC) {
			this.capacity = c;			
		}
		this.gto = gto;
		//file
		this.scanner = new BufferedReader(new FileReader(fpIn.getFD()));
		this.printerOut = new PrintWriter(new BufferedOutputStream(fpOut));
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
	public abstract void runPartition() throws NumberFormatException, IOException;



	public GraphPartitionator getGraphPartitionator() {
		return graphPartitionator;
	}

	public int getNodeNumbers() {
		return nodeNumbers;
	}

	public int getEdgeNumbers() {
		return edgeNumbers;
	}
	/**
	 * @throws IOException
	 */
	protected void readFirstLine() throws IOException {
		String line;
		while ((line = scanner.readLine()) != null
				&&	line.length() != 0) {
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
	}
	
	public Long getPartitioningTime() {
		return this.partTime;
	}




}
