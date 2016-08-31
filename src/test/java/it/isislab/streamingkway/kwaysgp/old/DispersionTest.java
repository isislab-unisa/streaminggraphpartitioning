package it.isislab.streamingkway.kwaysgp.old;

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
public class DispersionTest 
extends TestCase
{

	private Logger log = Logger.getGlobal();

	private CSVWriter writer;
	
	public static final Integer ITERATION_TIME = 1;
	public static final Double MES_TOLERANCE = 0.06;
	public static final String PLACEHOLDER_B = "B";
	public static final String PLACEHOLDER_D = "D";
	public static final String PLACEHOLDER_R = "R";
	public static final String PATH_4ELT_GRAPH = "resources/4elt.graph";
	public static final String PATH_TINY_GRAPH = "resources/tiny_01.graph";
	@SuppressWarnings("unused")
	private static final Double DISPLACEMENT_TOLERANCE = 10.0;

	private static final String CSV_FILENAME = "resources/results.csv";


	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @throws IOException 
	 */
	public DispersionTest( String testName ) throws IOException
	{
		super( testName );
		File fp = new File(CSV_FILENAME);
		if (fp.exists()) {
			fp.delete();
		}
		writer = new CSVWriter(new FileWriter(CSV_FILENAME,true));
		String[] header =  {
						"Graph Name",
						"Total nodes", 	
						"Total edges",
						"Ordering Type",
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
		return new TestSuite( DispersionTest.class );
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
		//check 4elt with 4 partitions
		Integer k = 2;
		Integer C = 4; // 7/2+1
		allHeuristicsTestCompare(new File(PATH_TINY_GRAPH), new File(PATH_TINY_GRAPH + ".bfs"), k,
				C, Ordering.BFS_ORDER, "Tiny_01");
	}
	public void testTinyDFS() throws HeuristicNotFound, IOException, InterruptedException {

		//check 4elt with 4 partitions
		Integer k = 2;
		Integer C = 4; // 7/2+1
		allHeuristicsTestCompare(new File(PATH_TINY_GRAPH), new File(PATH_TINY_GRAPH + ".dfs"), k,
				C, Ordering.DFS_ORDER, "Tiny_01");
	}
	public void testTinyRND() throws HeuristicNotFound, IOException, InterruptedException {

		//check 4elt with 4 partitions
		Integer k = 2;
		Integer C = 4; // 7/2+1
		allHeuristicsTestCompare(new File(PATH_TINY_GRAPH), new File(PATH_TINY_GRAPH + ".rnd"), k,
				C, Ordering.RANDOM_ORDER, "Tiny_01");
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
		//check 4elt with 4 partitions
		Integer k = 4;
		Integer C = 3902; // 15606/4+1

		allHeuristicsTestCompare(new File(PATH_4ELT_GRAPH), new File(PATH_4ELT_GRAPH + ".bfs"), 
				k, C, Ordering.BFS_ORDER, "4elt");
	}
	public void test4eltDFS() throws HeuristicNotFound, IOException, InterruptedException {
		//check 4elt with 4 partitions
		Integer k = 4;
		Integer C = 3902; // 15606/4+1
		allHeuristicsTestCompare(new File(PATH_4ELT_GRAPH), new File(PATH_4ELT_GRAPH + ".dfs"), 
				k, C, Ordering.DFS_ORDER, "4elt");
	}
	public void test4eltRND() throws HeuristicNotFound, IOException, InterruptedException {
		//check 4elt with 4 partitions
		Integer k = 4;
		Integer C = 3902; // 15606/4+1
		allHeuristicsTestCompare(new File(PATH_4ELT_GRAPH), new File(PATH_4ELT_GRAPH + ".rnd"), 
				k, C, Ordering.RANDOM_ORDER, "4elt");
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
	private void allHeuristicsTestCompare(File file1, File file2, Integer k, Integer C, 
			String glType, String graphName) throws HeuristicNotFound, IOException, InterruptedException {
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
				FileInputStream fpIn = new FileInputStream(file1);
				FileOutputStream fpOut = new FileOutputStream(file2);
				heuristic = HeuristicFactory.getHeuristic(i);
				gl = getGraphLoader(glType, fpIn,fpOut,k,heuristic,C,true);
				Thread.sleep(500);
				Long startTime = System.currentTimeMillis();
				gl.runPartition(); 
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
				fpIn.close();
				fpOut.close();

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
				FileInputStream fpIn = new FileInputStream(file1);
				FileOutputStream fpOut = new FileOutputStream(file2);
				heuristic = HeuristicFactory.getHeuristic(i);
				gl = getGraphLoader(glType, fpIn,fpOut,k,heuristic,C,true);
				Thread.sleep(1500);
				Long startTime = System.currentTimeMillis();
				gl.runPartition();
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
				fpIn.close();
				fpOut.close();
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
		writer.close();
	}



	private GraphLoader getGraphLoader(String glType, FileInputStream fpIn, FileOutputStream fpOut, Integer k, 
			SGPHeuristic heuristic, Integer c, boolean thereIsC) throws IOException {
		GraphTraversingOrdering gto = OrderingFactory.getOrdering(glType);
		return new TraversingGraphLoader(fpIn, fpOut, k, heuristic, c, thereIsC, gto);
	}

	private void saveCSV (String[] toSave) throws IOException {
		writer.writeNext(toSave);
		writer.flush();
	}
	
}
