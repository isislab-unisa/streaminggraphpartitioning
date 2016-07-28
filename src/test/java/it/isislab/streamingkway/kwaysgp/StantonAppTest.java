package it.isislab.streamingkway.kwaysgp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;
import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.Ordering;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.heuristics.Heuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class StantonAppTest 
extends TestCase
{

	private Logger log = Logger.getGlobal();
	
	public static final Integer ITERATION_TIME = 5;
	public static final Double MES_TOLERANCE = 0.06;
	public static final String PLACEHOLDER_B = "B";
	public static final String PLACEHOLDER_D = "D";
	public static final String PLACEHOLDER_R = "R";
	public static final String PATH_4ELT_ST_RES = "resources/4elt-stanton-res";
	public static final String PATH_4ELT_GRAPH = "resources/4elt.graph";
	public static final String PATH_TINY_GRAPH = "resources/tiny_01.graph";
	private static final Double DISPLACEMENT_TOLERANCE = 5.0;
	
	Map<Integer, Double> res4eltBfs = new HashMap<>();
	Map<Integer, Double> res4eltDfs = new HashMap<>();
	Map<Integer, Double> res4eltRan = new HashMap<>();

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @throws IOException 
	 */
	public StantonAppTest( String testName ) throws IOException
	{
		super( testName );
		init4EltMap();
	}

	private void init4EltMap() throws IOException {
		File csvIn = new File(PATH_4ELT_ST_RES);
		loadCSV(csvIn, res4eltBfs, res4eltDfs, res4eltRan);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( StantonAppTest.class );
	}
	/********************************************************************************
	 * 
	 * 		TEST FOR TINY_01 GRAPH
	 * 		1. BFS
	 * 		2. DFS
	 * 		3. RANDOM
	 * 
	 * ******************************************************************************
	 */
	public void testTinyBFS() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(PATH_TINY_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(PATH_TINY_GRAPH + ".bfs");

		//check 4elt with 4 partitions
		Integer k = 2;
		Integer C = 4; // 7/2+1

		allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.BFS_ORDER,null);
	}
	public void testTinyDFS() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(PATH_TINY_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(PATH_TINY_GRAPH + ".dfs");

		//check 4elt with 4 partitions
		Integer k = 2;
		Integer C = 4; // 7/2+1

		allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.DFS_ORDER,null);
	}
	public void testTinyRND() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(PATH_TINY_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(PATH_TINY_GRAPH + ".rnd");

		//check 4elt with 4 partitions
		Integer k = 2;
		Integer C = 4; // 7/2+1

		allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.RANDOM_ORDER, null);
	}
	/********************************************************************************
	 * 
	 * 		TEST FOR TINY_01 GRAPH ENDED
	 * 
	 * ******************************************************************************
	 */
	
	/******************************************************************************
	 * 
	 * 		TEST FOR 4ELT GRAPH
	 * 		1. BFS
	 * 		2. DFS
	 * 		3. RANDOM
	 * *
	 * @throws IOException 
	 * @throws HeuristicNotFound ****************************************************************************
	 */
	public void test4eltBFS() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(PATH_4ELT_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(PATH_4ELT_GRAPH + ".bfs");

		//check 4elt with 4 partitions
		Integer k = 4;
		Integer C = 3902; // 15606/4+1

		allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.BFS_ORDER, res4eltBfs);
	}
	public void test4eltDFS() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(PATH_4ELT_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(PATH_4ELT_GRAPH +".dfs");

		//check 4elt with 4 partitions
		Integer k = 4;
		Integer C = 3902; // 15606/4+1

		allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.DFS_ORDER, res4eltDfs);
	}
	public void test4eltRND() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(PATH_4ELT_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(PATH_4ELT_GRAPH + ".rnd");

		//check 4elt with 4 partitions
		Integer k = 4;
		Integer C = 3902; // 15606/4+1

		allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.RANDOM_ORDER, res4eltRan);
	}
	/******************************************************************************
	 * 
	 * 		TEST FOR 4ELT GRAPH ENDED
	 * 
	 * *****************************************************************************
	 */
	
	/*
	 * **
	 * ** 		UTILITY METHODS
	 * ** 
	 */
	private void allHeuristicsTestCompare(File fpIn, File fpOut, Integer k, Integer C, String glType,
			Map<Integer, Double> toCompareResults) 
					throws HeuristicNotFound, IOException, InterruptedException {
		HashMap<Integer, Double> heuristicEdgesRatio = new HashMap<>();
		GraphLoader gl = null; 

		//check all heuristics
		Field[] heuristics = Heuristic.class.getDeclaredFields();
		QualityChecker qc = new ParallelQualityChecker();
		for (int i = 1; i <= heuristics.length; i++) {
			heuristicEdgesRatio.put(i, 0.0); //init entry
			log.info("Executing: " + HeuristicFactory.getHeuristic(i).getHeuristicName());
			for (int j = 0; j < ITERATION_TIME; j++) {
				gl = getGraphLoader(glType, fpIn,fpOut,k,HeuristicFactory.getHeuristic(i),C,true);
				Thread.sleep(500);
				gl.run();    
				double edgesRatio = qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph());
				edgesRatio += heuristicEdgesRatio.get(i);
				heuristicEdgesRatio.put(i, edgesRatio);
				//count total partitioned nodes
				int totalNodes = gl.getGraphPartitionator().getTotalPartitionedNodes();
				assertEquals(totalNodes, gl.getNodeNumbers());
				assertEquals(totalNodes,gl.getGraphPartitionator().getGraph().getNodeCount());
				//count total partitioned edges
				int totalEdges = gl.getEdgeNumbers();
				assertEquals(totalEdges, gl.getGraphPartitionator().getGraph().getEdgeCount());
				//check displacement
				assertTrue(qc.getDisplacement(gl.getGraphPartitionator().getPartitionMap()) <= 
						DISPLACEMENT_TOLERANCE);
			}
			log.info("Test for " + HeuristicFactory.getHeuristic(i).getHeuristicName() + " done.");
			heuristicEdgesRatio.put(i, heuristicEdgesRatio.get(i) / ITERATION_TIME);
		}
		if (toCompareResults != null) {
			checkResults(heuristicEdgesRatio, toCompareResults );			
		}
	}


	private void checkResults(Map<Integer, Double> heuristicEdgesRatio,
			Map<Integer, Double> toCompareResults) {
		assertTrue(toCompareResults.size() == heuristicEdgesRatio.size());

		for (Entry<Integer, Double> val : heuristicEdgesRatio.entrySet()) {
			boolean gt = Double.compare(val.getValue(),toCompareResults.get(val.getKey()) + MES_TOLERANCE) <= 0;
			log.info("Comparing: " + val.getValue() + " vs " + toCompareResults.get(val.getKey()) +
					" with " + HeuristicFactory.getHeuristic(val.getKey()).getHeuristicName());
			assertTrue("Comparing results",gt);
		}
	}


	private GraphLoader getGraphLoader(String glType, File fpIn, File fpOut, Integer k, 
			SGPHeuristic heuristic, Integer c, boolean thereIsC) throws IOException {
		GraphTraversingOrdering gto = OrderingFactory.getOrdering(glType);
		return new TraversingGraphLoader(fpIn, fpOut, k, heuristic, c, thereIsC, gto);
	}

	private void loadCSV(File fpIn, Map<Integer,Double> mapForB, Map<Integer,Double> mapForD,
			Map<Integer,Double> mapForR ) throws IOException {
		assertTrue(fpIn.exists());
		CSVReader reader = new CSVReader(new FileReader(fpIn));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String order = nextLine[1].trim();
			switch (order) {
			case PLACEHOLDER_B : mapForB.put(Integer.parseInt(nextLine[0]), Double.parseDouble(nextLine[2]));
								break;
			case PLACEHOLDER_D : mapForD.put(Integer.parseInt(nextLine[0]), Double.parseDouble(nextLine[2]));
								break;
			case PLACEHOLDER_R : mapForR.put(Integer.parseInt(nextLine[0]), Double.parseDouble(nextLine[2]));
								break;
			}
		}
		assertTrue(mapForB.size() > 0);
		assertEquals(mapForB.size(), mapForD.size());
		assertEquals(mapForD.size(), mapForR.size());
		reader.close();
	}

}
