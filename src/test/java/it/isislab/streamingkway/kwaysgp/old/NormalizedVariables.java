package it.isislab.streamingkway.kwaysgp.old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import au.com.bytecode.opencsv.CSVWriter;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.BFSTraversing;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.relationship.LinearNormWeightedDispersionBased;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;

public class NormalizedVariables {


	private static final String FOLDER = "resources/";
	private static final Integer ITER_TIME = 5;
	private static final Double STEP = 0.1;
	private static final Integer MAX_PARTS = 128;
	CSVWriter writer; 
	Logger log = Logger.getGlobal();
	String[] header = {
			"Graph Name",
			"Total nodes", 	
			"Total edges",
			"K",
			"A",
			"B",
			"C",
			"Cutted Edges",
			"Cutted Edges Ratio",
			"Iteration time"
	};

	@Test
	public void test() throws FileNotFoundException, IOException {
		File fold = new File(FOLDER);
		for (File f : fold.listFiles(p -> p.getName().endsWith(".graph"))) {
			log.info("Test for : " + f.getName());
			writer = new CSVWriter(new FileWriter(new File(f.getName() + ".csv")));
			writer.writeNext(header);
			for (int k = 2; k < MAX_PARTS; k*=2) 
				for (double A = 0.1; A < 10.0; A+=STEP) {
					for (double B = 0.1; B < 10.0; B+= STEP) {
						for (double C = 0.1; C < 10.0; C+=STEP) {
							log.info("Testing for A: " + A + ", B: " + B + " , C: " + C);
							for (int i = 0; i < ITER_TIME; i++) {
								SGPHeuristic heuristic = new LinearNormWeightedDispersionBased(A, B,C);
								GraphLoader gl = new TraversingGraphLoader(new FileInputStream(f), new FileOutputStream(f+"-r"),
										k, heuristic, -1, false, new BFSTraversing());
								gl.run();
								QualityChecker qc = new ParallelQualityChecker();
								String[] res = {
										f.getName(),
										Integer.toString(gl.getNodeNumbers()),
										Integer.toString(gl.getEdgeNumbers()),
										Integer.toString(k),
										Double.toString(A),
										Double.toString(B),
										Double.toString(C),
										qc.getCuttingEdgesCount(gl.getGraphPartitionator().getGraph()).toString(),
										qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph()).toString(),
										ITER_TIME.toString()
								};
								writer.writeNext(res);
								writer.flush();
							}
						}
					}
				}
				writer.close();
			
		}

	}


}
