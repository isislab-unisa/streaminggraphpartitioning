package it.isislab.streamingkway.kwaysgp.finaltest;

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
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AllGraphsTest 
extends TestCase implements HeuristicsTest
{


	public static final Double MES_TOLERANCE = 0.06;
	public static final String PLACEHOLDER_B = "B";
	public static final String PLACEHOLDER_D = "D";
	public static final String PLACEHOLDER_R = "R";
	private static final String CSV_SUFFIX = "-res.csv";
	private static final int MAX_PARTITION_SIZE = 128;
	private static final String FOLDER = "resources/";


	private Logger log = Logger.getGlobal();
	private CSVWriter writer;
	/** 
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @throws IOException 
	 */
	public AllGraphsTest( String testName ) throws IOException
	{
		super( testName );
	}


	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( AllGraphsTest.class );
	}


	public void testStreet() throws HeuristicNotFound, IOException, InterruptedException, IllegalArgumentException, IllegalAccessException {
		Field[] ords = Ordering.class.getFields();
		File fold = new File(FOLDER);
		//seq
		for (File fpin: fold.listFiles(p -> p.getName().endsWith(".graph"))) {
			File fpout = new File(FOLDER +"toremove-out");
			writer = new CSVWriter(new FileWriter(new File(FOLDER + fpin.getName() + CSV_SUFFIX)),' ');
			Integer C = -1;
			for (int oi = ords.length-1; oi >= 0; oi--) {
				writer.writeNext(HEADER);
				String ord = (String)ords[oi].get(new Ordering());
				for (int k = 2; k <= MAX_PARTITION_SIZE; k*=2) {
					log.info("Test for: " + fpin.getName() + " with "+k+
							"partitions using " + ord +" started");
					myAllHeuristicsTestCompare(fpin, fpout, k, C, ord, 
							fpin.getName(), false);
				}
			}
			writer.close();
		}
		//par
		for (File fpin: fold.listFiles(p -> p.getName().endsWith("a.graph"))) {
			File fpout = new File(FOLDER +"toremove-out");
			writer = new CSVWriter(new FileWriter(new File(FOLDER + fpin.getName() + CSV_SUFFIX)),' ');
			Integer C = -1;
			for (int oi = ords.length-1; oi >= 0; oi--) {
				writer.writeNext(HEADER);
				String ord = (String)ords[oi].get(new Ordering());
				for (int k = 2; k <= MAX_PARTITION_SIZE; k*=2) {
					log.info("Test for: " + fpin.getName() + " with "+k+
							"partitions using " + ord +" started");
					myAllHeuristicsTestCompare(fpin, fpout, k, C, ord, 
							fpin.getName(),true);
				}
			}
			writer.close();
		}
	}


	private void myAllHeuristicsTestCompare(File fpIn, File fpOut, Integer k, Integer C, String glType, 
			String graphName, boolean par) throws HeuristicNotFound, IOException, InterruptedException {
		
		
		allHeuristicsTestCompare(fpIn, fpOut, k, C, glType, graphName, log, par);



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
