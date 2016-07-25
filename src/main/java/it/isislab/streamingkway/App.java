package it.isislab.streamingkway;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.graphstream.graph.Graph;

import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.SimpleGraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;

public class App {

	/**
	 * Expected usage:
	 * app file.graph k heuristicNumber [B|D|R|L] C
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if (args.length < 3) {
			System.err.println("usage: app file.graph k heuristicNumber [C] [B|D|R|L]");
			System.exit(0);
		} else if (args.length < 2) {
			if (args[0].equalsIgnoreCase("help")) {
				//TODO to write all heuristics number and a little help
			}
		}
		//load user input
		boolean thereIsC = false; //true if the user put a value for C
		String fileName = args[0];
		Integer k = Integer.parseInt(args[1]);
		Integer heuristicNumber = Integer.parseInt(args[2]);
		Integer C = Integer.MAX_VALUE;
		GraphTraversingOrdering gto = null;
		if (args.length > 3) {
			gto = OrderingFactory.getOrdering(args[3]);
		}
		if (args.length > 4) {
			C = Integer.parseInt(args[4]);
			thereIsC = true;
		}
		
		//load files
		File fpIn = new File(fileName);
		String fileOutName = "res-"+fileName+"-"+new Date().getTime()+"-e"+heuristicNumber;
		File fpOut = new File(fileOutName);
		//load heuristic
		SGPHeuristic heuristic = null;
		try {
			heuristic =  HeuristicFactory.getHeuristic(heuristicNumber);
		} catch (HeuristicNotFound e1) {
			System.err.println("Heuristic " + heuristicNumber + " does not exists");
			System.err.println(e1.getMessage());
			//TODO print help
		}
		
		
		//create graph loader
		GraphLoader gl = null;
		try {
			if (gto == null) {
				gl = new SimpleGraphLoader(fpIn, fpOut, k, heuristic, C, thereIsC);				
			} else {
				gl = new TraversingGraphLoader(fpIn, fpOut, k, heuristic, C, thereIsC, gto);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Not a valid filename");
			e.printStackTrace();
		}
		
		Long startDate = System.currentTimeMillis();
		gl.run(); //partition started
		Long endDate = System.currentTimeMillis();
		
		GraphPartitionator gp = gl.getGraphPartitionator();
		Graph gr = gp.getGraph();
		Long startQCDate = System.nanoTime();
		QualityChecker qc = new ParallelQualityChecker();
		Long endQCDate = System.nanoTime();
		
		System.out.println("Heuristic used: " + heuristic.getHeuristicName());
		System.out.println("Total nodes: " + gr.getNodeCount());
		System.out.println("Total edges: " + gr.getEdgeCount());
		System.out.println("Cutting-edges: "+ qc.getCuttingEdgesCount(gr));
		System.out.println("Time spent for partitioning: " + (endDate- startDate) + "ms");
		System.out.println("Time spent for quality checking: " + (endQCDate - startQCDate) +"ns");
		System.out.println("Get total partitioned nodes: " + gp.getTotalPartitionedNodes());
		
		
		//FIXME to separate
		File fpMetrics = new File("metrics.csv");
		String toPrint = new Date().toString() + "," + gr.getNodeCount() + ","+gr.getEdgeCount() +","
				+ qc.getCuttingEdgesCount(gr) + "," + (endDate - startDate);
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fpMetrics,true)));
		if (! fpMetrics.exists() || fpMetrics.length() == 0.0) {
			String header = "DATE,HEURISTIC,TOTAL_NODES,TOTAL_EDGES,CUTTING_EDGES,TIME_SPENT";
			printWriter.println(header);
		}
		System.out.println(toPrint);
		printWriter.println(toPrint);
		printWriter.flush();
		printWriter.close();
		gr.display();
	}
	
}
