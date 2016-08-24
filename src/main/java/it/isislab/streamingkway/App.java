package it.isislab.streamingkway;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.SimpleGraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;

public class App {

	@Option(name="-k",usage="Sets the number of partitions", required=true)
	Integer k;
	@Option(name="-h",usage="Sets heuristic number", required=true)
	Integer heuristicNumber;
	@Option(name="-c", usage="Sets the capacity of each partition. Default is (n/k)+1")
	Integer C;
	@Option(name="-g", usage="Input graph file", metaVar="INPUT GRAPH", required=true)
	String inputFn;
	@Option(name="-o", usage="Output file. If not present the name will be [input-graph-file].part.[k]",metaVar="OUTPUT FILE", required=false)
	String outputFn;
	@Option(name="-t", usage="Sets traversing order", required=false)
	String gt;
	@Option(name="--view", usage="Display the graph", required=false)
	Boolean view = false;

	public static void main(String[] args)  {

		new App().start(args);
	}

	private void start(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			parser.printUsage(System.out);
			return;
		}
		boolean thereIsC = false;
		if (C != null) {
			thereIsC = true;
		}
		FileInputStream fpIn = null;
		try {
			fpIn = new FileInputStream(new File(inputFn));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		FileOutputStream fpOut = null;
		try {
			if (outputFn != null) {
				fpOut = new FileOutputStream(new File(outputFn));
			} else {
				fpOut = new FileOutputStream(new File(inputFn + ".part." + k));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		SGPHeuristic heuristic = HeuristicFactory.getHeuristic(heuristicNumber);
		GraphTraversingOrdering gto = null;
		GraphLoader gl = null;
		try {
			if (gt != null) {
				gto = OrderingFactory.getOrdering(gt);
			}
			if (gto != null) {
				gl = new TraversingGraphLoader(fpIn, fpOut, k, heuristic, C, thereIsC, gto);
			} else {
				gl = new SimpleGraphLoader(fpIn, fpOut, k, heuristic, C, thereIsC);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long startTime = System.currentTimeMillis();
		gl.run();
		Long endTime = System.currentTimeMillis();
		System.out.println("Done!");
		qualityCheck(gl, (endTime- startTime),heuristic, System.out);

		if (view) {
			gl.getGraphPartitionator().getGraph().display();
		}
	}

	private void qualityCheck(GraphLoader gl, Long timeSpent,SGPHeuristic heuristic, PrintStream outStream) {
		QualityChecker qc = new ParallelQualityChecker();

		outStream.println(heuristic.getHeuristicName());
		outStream.println("Time spent: " + timeSpent + "ms");
		outStream.println("Total edges: " + gl.getEdgeNumbers());
		outStream.println("Total nodes: " + gl.getNodeNumbers());
		outStream.println("Cutting edges: " +qc.getCuttingEdgesCount(gl.getGraphPartitionator().getGraph()));
		outStream.printf("Edge ratio: %.3f%% %n", qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph())*100);
		outStream.println("Maximimum Normalized Load: " + qc.getNormalizedMaximumLoad(gl.getGraphPartitionator().getPartitionMap(), gl.getGraphPartitionator().getGraph()));
	}


}
