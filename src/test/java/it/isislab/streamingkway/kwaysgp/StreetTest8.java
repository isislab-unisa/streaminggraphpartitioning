package it.isislab.streamingkway.kwaysgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.Ordering;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class StreetTest8 
extends TestCase implements HeuristicsTest
{

	private Logger log = Logger.getGlobal();

	private CSVWriter writer;
	
	public static final Double MES_TOLERANCE = 0.06;
	public static final String PLACEHOLDER_B = "B";
	public static final String PLACEHOLDER_D = "D";
	public static final String PLACEHOLDER_R = "R";
	public static final String TEST_STREET_GRAPH = "resources/luxembourg.osm.graph";
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
	public StreetTest8( String testName ) throws IOException
	{
		super( testName );
		writer = new CSVWriter(new FileWriter(CSV_FILENAME,true));
		writer.writeNext(HEADER);
	}


	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( StreetTest8.class );
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
			myAllHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.BFS_ORDER, TEST_STREET_GRAPH);			
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
			myAllHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.DFS_ORDER, TEST_STREET_GRAPH);
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
			myAllHeuristicsTestCompare(fpIn, fpOut, k, C, Ordering.RANDOM_ORDER, TEST_STREET_GRAPH);
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
	private void myAllHeuristicsTestCompare(File fpIn, File fpOut, Integer k, Integer C, String glType, 
			String graphName) throws HeuristicNotFound, IOException, InterruptedException {
		
		allHeuristicsTestCompare(fpIn, fpOut, k, C, glType, graphName, log);
	}


	public GraphLoader getGraphLoader(String glType, File fileIn, File fileOut, Integer k, 
			SGPHeuristic heuristic, Integer c, boolean thereIsC) throws IOException {
		GraphTraversingOrdering gto = OrderingFactory.getOrdering(glType);
		FileInputStream fpIn = new FileInputStream(fileIn);
		FileOutputStream fpOut = new FileOutputStream(fileOut);
		return new TraversingGraphLoader(fpIn, fpOut, k, heuristic, c, thereIsC, gto);
	}

	public void saveCSV (String[] toSave) throws IOException {
		writer.writeNext(toSave);
		writer.flush();
	}


	public void myAssertEquals(int intValue, int nodeNumbers) {
		assertEquals(intValue, nodeNumbers);
	}
	
}
