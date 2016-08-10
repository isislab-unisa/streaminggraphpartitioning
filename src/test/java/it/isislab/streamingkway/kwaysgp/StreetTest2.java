package it.isislab.streamingkway.kwaysgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.Ordering;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.heuristics.Heuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.heuristics.relationship.RelationshipHeuristics;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class StreetTest2 
extends TestCase
{

	private Logger log = Logger.getGlobal();

	private CSVWriter writer;
	
	public static final Integer CPU_REFRESH_TIME = 0;
	public static final Integer ITERATION_TIME = 20;
	public static final Double MES_TOLERANCE = 0.06;
	public static final String PLACEHOLDER_B = "B";
	public static final String PLACEHOLDER_D = "D";
	public static final String PLACEHOLDER_R = "R";
	public static final String TEST_STREET_GRAPH = "resources/delaunay_n11.graph";
	@SuppressWarnings("unused")
	private static final Double DISPLACEMENT_TOLERANCE = 10.0;

	private static final String CSV_FILENAME = TEST_STREET_GRAPH + "-res.csv";

	private static final int MAX_PARTITION_SIZE = 128;


	/** 
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @throws IOException 
	 */
	public StreetTest2( String testName ) throws IOException
	{
		super( testName );
		writer = new CSVWriter(new FileWriter(CSV_FILENAME,true));
		String[] header =  {
						"Graph Name",
						"Total nodes", 	
						"Total edges",
						"Ordering Type",
						"K",
						"Heuristic Name",  
						"Displacement", 	
						"Cutted Edges",
						"Cutted Edges Ratio",
						"Total time",
						"Iteration time"
				};
		writer.writeNext(header);
	}


	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( StreetTest2.class );
	}
	
	/******************************************************************************
	 * 
	 * 		TEST FOR GRAPH
	 * 		1. BFS
	 * 		2. DFS
	 * 		3. RANDOM
	 * *
	 * @throws IOException 
	 * @throws HeuristicNotFound ****************************************************************************
	 */
	public void testStreetBFS() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(TEST_STREET_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(TEST_STREET_GRAPH + ".bfs");

		//check 4elt with 4 partitions
		Integer C = -1; // 15606/4+1
		for (int k = 2; k <= MAX_PARTITION_SIZE; k*=2) {
			allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.BFS_ORDER, TEST_STREET_GRAPH);			
		}
		writer.close();
	}
	public void testStreetDFS() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(TEST_STREET_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(TEST_STREET_GRAPH +".dfs");

		//check 4elt with 4 partitions
		Integer C = -1; // 15606/4+1
		for (int k = 2; k <= MAX_PARTITION_SIZE; k*=2) {
			allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.DFS_ORDER, TEST_STREET_GRAPH);
		}
		writer.close();
	}
	public void testStreetRND() throws HeuristicNotFound, IOException, InterruptedException {
		File fpIn = new File(TEST_STREET_GRAPH);
		assertTrue(fpIn.exists());
		File fpOut = new File(TEST_STREET_GRAPH + ".rnd");

		//check 4elt with 4 partitions
		Integer C = -1; // 15606/4+1
		for (int k = 2; k <= MAX_PARTITION_SIZE; k*=2) {
			allHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.RANDOM_ORDER, TEST_STREET_GRAPH);
		}
		writer.close();
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
			String graphName) throws HeuristicNotFound, IOException, InterruptedException {
		Double heuristicEdgesRatio = 0.0;
		Double cuttedEdges = 0.0;
		Double displacement = 0.0;
		Double normalizedMaxLoad = 0.0;
		Long totalTime = 0l;
		GraphLoader gl = null; 

		//check all heuristics
		Field[] heuristics = Heuristic.class.getDeclaredFields();
		Field[] relHeuristics = RelationshipHeuristics.class.getDeclaredFields();
//		ArrayList<Field> allHeuristics = new ArrayList<>(heuristics.length + relHeuristics.length);
//		for (int i = 0; i < heuristics.length; i++) {
//			allHeuristics.add(heuristics[i]);
//		}
//		for (int i = 0; i < relHeuristics.length; i++) {
//			allHeuristics.add(relHeuristics[i]);
//		}
		
		log.info("Testing for k= "+k );
		QualityChecker qc = new ParallelQualityChecker();
		for (int i = 1; i <= heuristics.length; i++) {
			cuttedEdges = 0.0;
			displacement = 0.0;
			totalTime = 0l;
			normalizedMaxLoad = 0.0;
			heuristicEdgesRatio = 0.0; //init entry
			
			log.info("Executing: " + HeuristicFactory.getHeuristic(i).getHeuristicName());
			Integer totalNodes = 0;
			Integer totalEdges = 0;
			SGPHeuristic heuristic = null;
			for (int j = 0; j < ITERATION_TIME; j++) {
				heuristic = HeuristicFactory.getHeuristic(i);
				gl = getGraphLoader(glType, fpIn,fpOut,k,heuristic,C,false);
				Thread.sleep(CPU_REFRESH_TIME);
				Long startTime = System.currentTimeMillis();
				gl.run(); 
				Long endTime = System.currentTimeMillis();
				totalTime += (endTime - startTime);
				heuristicEdgesRatio += qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph());
				cuttedEdges += qc.getCuttingEdgesCount(gl.getGraphPartitionator().getGraph());
				//count total partitioned nodes
				totalNodes = gl.getGraphPartitionator().getTotalPartitionedNodes();
				assertEquals(totalNodes.intValue(), gl.getNodeNumbers());
				assertEquals(totalNodes.intValue(),gl.getGraphPartitionator().getGraph().getNodeCount());
				//count total partitioned edges
				totalEdges = gl.getEdgeNumbers();
				assertEquals(totalEdges.intValue(), gl.getGraphPartitionator().getGraph().getEdgeCount());
				//check displacement
				displacement += qc.getDisplacement(gl.getGraphPartitionator().getPartitionMap());
				//assertTrue(displacement <= DISPLACEMENT_TOLERANCE);
				//check normalized maximum load
				normalizedMaxLoad += qc.getNormalizedMaximumLoad(gl.getGraphPartitionator().getPartitionMap(), 
						gl.getGraphPartitionator().getGraph());
			}
			heuristicEdgesRatio /= ITERATION_TIME;
			cuttedEdges /= ITERATION_TIME;
			displacement /= ITERATION_TIME;
			normalizedMaxLoad /= ITERATION_TIME;
			String[] metrics = {
					graphName,						//graph name
					totalNodes.toString(), 			//total nodes
					totalEdges.toString(), 			//total	edges
					glType,							//gl type
					k.toString(),					//k
					heuristic.getHeuristicName(),  //heuristic name
					displacement.toString(), 		//displacement
					cuttedEdges.toString(),			//cutted edges
					heuristicEdgesRatio.toString(),	//edges ratio
					totalTime.toString(),
					ITERATION_TIME.toString()
			};
			log.info("Metrics: " + metrics);
			saveCSV(metrics);
			log.info("Test for " + HeuristicFactory.getHeuristic(i).getHeuristicName() + " done.");
		}
		
		for (int relI = 31; relI < 31 + relHeuristics.length; relI++) {
			
			int i = relI;
					
			cuttedEdges = 0.0;
			displacement = 0.0;
			totalTime = 0l;
			normalizedMaxLoad = 0.0;
			heuristicEdgesRatio = 0.0; //init entry
			
			log.info("Executing: " + HeuristicFactory.getHeuristic(i).getHeuristicName());
			Integer totalNodes = 0;
			Integer totalEdges = 0;
			SGPHeuristic heuristic = null;
			for (int j = 0; j < ITERATION_TIME; j++) {
				heuristic = HeuristicFactory.getHeuristic(i);
				gl = getGraphLoader(glType, fpIn,fpOut,k,heuristic,C,false);
				Thread.sleep(CPU_REFRESH_TIME);
				Long startTime = System.currentTimeMillis();
				gl.run(); 
				Long endTime = System.currentTimeMillis();
				totalTime += (endTime - startTime);
				heuristicEdgesRatio += qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph());
				cuttedEdges += qc.getCuttingEdgesCount(gl.getGraphPartitionator().getGraph());
				//count total partitioned nodes
				totalNodes = gl.getGraphPartitionator().getTotalPartitionedNodes();
				assertEquals(totalNodes.intValue(), gl.getNodeNumbers());
				assertEquals(totalNodes.intValue(),gl.getGraphPartitionator().getGraph().getNodeCount());
				//count total partitioned edges
				totalEdges = gl.getEdgeNumbers();
				assertEquals(totalEdges.intValue(), gl.getGraphPartitionator().getGraph().getEdgeCount());
				//check displacement
				displacement += qc.getDisplacement(gl.getGraphPartitionator().getPartitionMap());
				//assertTrue(displacement <= DISPLACEMENT_TOLERANCE);
				//check normalized maximum load
				normalizedMaxLoad += qc.getNormalizedMaximumLoad(gl.getGraphPartitionator().getPartitionMap(), 
						gl.getGraphPartitionator().getGraph());
			}
			heuristicEdgesRatio /= ITERATION_TIME;
			cuttedEdges /= ITERATION_TIME;
			displacement /= ITERATION_TIME;
			normalizedMaxLoad /= ITERATION_TIME;
			String[] metrics = {
					graphName,						//graph name
					totalNodes.toString(), 			//total nodes
					totalEdges.toString(), 			//total	edges
					glType,							//gl type
					k.toString(),					//k
					heuristic.getHeuristicName(),  //heuristic name
					displacement.toString(), 		//displacement
					cuttedEdges.toString(),			//cutted edges
					heuristicEdgesRatio.toString(),	//edges ratio
					totalTime.toString(),
					ITERATION_TIME.toString()
			};
			saveCSV(metrics);
			log.info("Test for " + HeuristicFactory.getHeuristic(i).getHeuristicName() + " done.");
		}
		
	}


	private GraphLoader getGraphLoader(String glType, File fileIn, File fileOut, Integer k, 
			SGPHeuristic heuristic, Integer c, boolean thereIsC) throws IOException {
		GraphTraversingOrdering gto = OrderingFactory.getOrdering(glType);
		FileInputStream fpIn = new FileInputStream(fileIn);
		FileOutputStream fpOut = new FileOutputStream(fileOut);
		return new TraversingGraphLoader(fpIn, fpOut, k, heuristic, c, thereIsC, gto);
	}

	private void saveCSV (String[] toSave) throws IOException {
		writer.writeNext(toSave);
		writer.flush();
	}
	
}
