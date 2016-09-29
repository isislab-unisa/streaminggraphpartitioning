package it.isislab.streamingkway.graphloaders;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;

import it.isislab.streamingkway.graphpartitionator.StreamingGraphPartitionator;
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
public class SimpleGraphLoader extends AbstractGraphLoader {


	public SimpleGraphLoader(FileInputStream fpIn, FileOutputStream fpOut, Integer k, SGPHeuristic heuristic, Integer c,
			boolean thereIsC) throws IOException {
		super(fpIn, fpOut, k, heuristic, c, thereIsC, null);
	}

	/**
	 * Performs the partitioning of the graph given by the {@link FileInputStream} in the constructor.
	 * It first tries to read the number of nodes and edges that the graph should contains, then starts
	 * loading and partitioning the graph according to the {@link SGPHeuristic} given in the constructor.
	 * For every node that load in the stream, it retrieve the partition in which it should be and then 
	 * assign the node to the partition and write this information on the {@link FileOutputStream} given to
	 * the constructor.
	 * @throws IOException 
	 * @throws EdgeRejectedException 
	 * @throws ElementNotFoundException 
	 * @throws IdAlreadyInUseException 
	 */
	public void runPartition() throws IdAlreadyInUseException, ElementNotFoundException, EdgeRejectedException, IOException {

		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		String line = "";
		readFirstLine();
		
		if (!thereIsC) {
			capacity = (int)Math.ceil((double)(nodeNumbers)/K);//+1;
		}
		//create graph
		this.graphPartitionator = new StreamingGraphPartitionator(K, heuristic, capacity);
		this.gr = graphPartitionator.getGraph();
		//read the whole graph
		
		while((line = scanner.readLine()) != null) {
			
			line = line.trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			}

			Long startTime = System.currentTimeMillis();
			String[] nNodes = line.split(" ");
			Node v = gr.addNode(Integer.toString(nodeCount++));
			gr.addNode(v.getId());

			for (String s : nNodes) {
				gr.addNode(s);
				gr.addEdge(v.getId()+"-"+s, v.getId(), s);
			}
			int uPartition = graphPartitionator.getPartitionNode(v);
			Long endTime = System.currentTimeMillis();
			partTime += (endTime - startTime);
			printerOut.println(uPartition);
		}

		printerOut.flush();
		printerOut.close();
		scanner.close();
	}

}
