package it.isislab.streamingkway.kwaysgp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.Ordering;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.heuristics.Heuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.heuristics.my.ParallelHeuristic;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParallelTest extends TestCase {

	private final static String GRAPH_PATH = "resources/4elt.graph";
	private final static Integer ITERATION_TIME = 5;
	private Logger log = Logger.getGlobal();
	private double parallelResult;
	private double serialResult;
	private Long parallelTime = 0L;
	private Long serialTime = 0L;
	
	public ParallelTest(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		return new TestSuite(ParallelTest.class);
	}	
	
	public void testParallel() throws IOException {
		File fpIn = new File(GRAPH_PATH);
		File fpOut = new File(GRAPH_PATH + ".res");
		
		GraphLoader gl = null;
		QualityChecker qc = new ParallelQualityChecker();
		//parallel test
		Integer k = 4;
		Integer c = 3902;
		SGPHeuristic heuristic = HeuristicFactory.getHeuristic(ParallelHeuristic.PARALLEL_U_DETERMINISTIC_G);
		GraphTraversingOrdering gto = OrderingFactory.getOrdering(Ordering.BFS_ORDER);
		for(int i = 0; i < ITERATION_TIME; i++) {
			Long startTime = System.currentTimeMillis();
			log.info("Test n." + (i+1) + " for parallel");
			gl = new TraversingGraphLoader(fpIn, fpOut, k, heuristic, c, true, gto);
			gl.run();
			Double.sum(parallelResult,qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph()));
			Long endTime = System.currentTimeMillis();
			parallelTime += (endTime - startTime);
		}
		parallelResult /= ITERATION_TIME;
		//serial test
		heuristic = HeuristicFactory.getHeuristic(Heuristic.U_DETERMINISTIC_GREEDY);
		gto = OrderingFactory.getOrdering(Ordering.BFS_ORDER);
		for(int i = 0; i < ITERATION_TIME; i++) {
			Long startTime = System.currentTimeMillis();
			log.info("Test n." + (i+1) + " for serial");
			gl = new TraversingGraphLoader(fpIn, fpOut, k, heuristic, c, true, gto);
			gl.run();
			Double.sum(serialResult,qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph()));
			Long endTime = System.currentTimeMillis();
			serialTime += (endTime - startTime);
		}
		serialResult /= ITERATION_TIME;
		
		log.info("Parallel time :" + parallelTime + "ms");
		log.info("Serial time :" + serialTime + "ms");
		assertTrue(Double.compare(serialResult, parallelResult) <= Double.MIN_VALUE*2);
		assertTrue(parallelTime < serialTime);
	}
	
	
}
