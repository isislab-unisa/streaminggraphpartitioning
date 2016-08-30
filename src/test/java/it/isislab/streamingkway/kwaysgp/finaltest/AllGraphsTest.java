package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;
import au.com.bytecode.opencsv.CSVWriter;
import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.SimpleGraphLoader;
import it.isislab.streamingkway.graphloaders.TraversingGraphLoader;
import it.isislab.streamingkway.graphloaders.factory.OrderingFactory;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.BFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.DFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.heuristics.Heuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.heuristics.relationship.RelationshipHeuristics;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
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
	private static final int MAX_PARTITION_SIZE = 32;
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
		File fold = new File(FOLDER);
		//seq
		for (File fpin: fold.listFiles(p -> p.getName().endsWith("k.graph"))) {
			String graphName = FOLDER + fpin.getName();
			
			String[] ords = {".bfs",".dfs",""};
			
			File fpout = new File(FOLDER +"toremove-out");
			writer = new CSVWriter(new FileWriter(new File(FOLDER + "/csv/" +fpin.getName() + CSV_SUFFIX)),' ');
			Integer C = -1;
			for (int i = 0; i < ITERATION_TIME; i++) {
				File graphNameBfs = new File(graphName + ".bfs." + i);
				File graphNameDfs = new File(graphName + ".dfs." + i);
				
				log.info("Sto creando il grafo BFS " + i);
				OrdinatorGraphLoader ogl = new OrdinatorGraphLoader(new FileInputStream(fpin), new FileOutputStream(graphNameBfs),
						new BFSTraversing());
				ogl.runPartition();
				log.info("Sto creando il grafo DFS" + i);
				OrdinatorGraphLoader ogld = new OrdinatorGraphLoader(new FileInputStream(fpin), new FileOutputStream(graphNameDfs),
						new DFSTraversing());
				ogld.runPartition();
				
			}
			for (int oi = 0; oi < ords.length; oi++) {
				String ord = ords[oi];
				File graphFile = new File(graphName+ord);
				writer.writeNext(HEADER);
				for (int k = 2; k <= MAX_PARTITION_SIZE; k*=2) {
					log.info("Test for: " + graphFile.getName() + " with "+k+
							"partitions using " + ord +" started");
					//allHeuristicsTestCompareSimple(graphFile, fpout, k, C, 
						//	fpin.getName(), log, true, ord == "" ? "rnd" : ord);
					allHeuristicsTestCompareSimple(graphFile, fpout, k, C, 
							fpin.getName(), log, false, ord == "" ? "rnd" : ord);
				}
			}
			writer.close();
		}
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


	private void allHeuristicsTestCompareSimple(File fpIn, File fpOut, Integer k, Integer C, 
			String graphName, Logger log, boolean par, String ord) throws HeuristicNotFound, IOException, InterruptedException {
		
		ArrayList<Double> heuristicEdgesRatio = null;
		ArrayList<Double> cuttedEdges = null;
		ArrayList<Double> displacement = null;
		ArrayList<Double> normalizedMaxLoad = null;
		ArrayList<Long> totalTime = null;

		
		Field[] heuristics = Heuristic.class.getDeclaredFields();
		Field[] relHeuristics = RelationshipHeuristics.class.getDeclaredFields();
		ArrayList<Integer> allHeuristics = new ArrayList<Integer>(heuristics.length + relHeuristics.length);
		for (int i = 0; i < heuristics.length; i++) {
			try {
				allHeuristics.add(heuristics[i].getInt(new Heuristic()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < relHeuristics.length; i++) {
			try {
				allHeuristics.add(relHeuristics[i].getInt(new RelationshipHeuristics()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		log.info("Testing for k= "+k );
		
		for (Integer i : allHeuristics) {
			heuristicEdgesRatio = new ArrayList<Double>();
			cuttedEdges = new ArrayList<Double>();
			displacement = new ArrayList<Double>();
			normalizedMaxLoad = new ArrayList<Double>();
			totalTime = new ArrayList<Long>();
			GraphLoader gl = null; 
			ParallelQualityChecker qc = new ParallelQualityChecker();

			Integer totalNodes = 0;
			Integer totalEdges = 0;
			SGPHeuristic heuristic = null;
			File fpInTest = null;
			for (int j = 0; j < ITERATION_TIME; j++) {
				if (!ord.equals("rnd")) {
					String fname = "resources/" + fpIn.getName();
					fname+= "."+j;
					fpInTest = new File(fname);
				} else {
					fpInTest = fpIn;
				}
				heuristic = HeuristicFactory.getHeuristic(i,par);
				log.info("Executing: " + heuristic.getHeuristicName());
				gl = new SimpleGraphLoader(new FileInputStream(fpInTest), new FileOutputStream(fpOut),
						k, heuristic, C, false);
				qc = new ParallelQualityChecker();
				Thread.sleep(250 + CPU_REFRESH_TIME);
				Long startTime = System.currentTimeMillis();
				gl.runPartition(); 
				Long endTime = System.currentTimeMillis();
				long time = endTime - startTime;
				totalTime.add(time);
				heuristicEdgesRatio.add(qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph()));
				cuttedEdges.add(0.0 + qc.getCuttingEdgesCount(gl.getGraphPartitionator().getGraph()));
				//count total partitioned nodes
				totalNodes = gl.getGraphPartitionator().getTotalPartitionedNodes();
				myAssertEquals(totalNodes.intValue(), gl.getNodeNumbers());
				myAssertEquals(totalNodes.intValue(), qc.getPartitionedNodeCount(gl.getGraphPartitionator().getGraph()));
				myAssertEquals(totalNodes.intValue(),gl.getGraphPartitionator().getGraph().getNodeCount());
				//count total partitioned edges
				totalEdges = gl.getEdgeNumbers();
				myAssertEquals((long)totalEdges.intValue(), qc.getTotalPartitionedEdges(gl.getGraphPartitionator().getGraph(),heuristic));
				myAssertEquals(totalEdges.intValue(), gl.getGraphPartitionator().getGraph().getEdgeCount());
				//check displacement
				displacement.add(qc.getDisplacement(gl.getGraphPartitionator().getPartitionMap()));
				//assertTrue(displacement <= DISPLACEMENT_TOLERANCE);
				//check normalized maximum load
				normalizedMaxLoad.add(qc.getNormalizedMaximumLoad(gl.getGraphPartitionator().getPartitionMap(), 
						gl.getGraphPartitionator().getGraph()));
			}
			Double avgheuristicEdgesRatio = heuristicEdgesRatio.stream().mapToDouble(p -> p.doubleValue()).average().getAsDouble(); 
			Double maxheuristicEdgesRatio = heuristicEdgesRatio.stream().mapToDouble(p -> p.doubleValue()).max().getAsDouble();
			Double minheuristicEdgesRatio = heuristicEdgesRatio.stream().mapToDouble(p -> p.doubleValue()).min().getAsDouble();
			Double avgcuttedEdges = cuttedEdges.stream().mapToDouble(p -> p.doubleValue()).average().getAsDouble();
			Double avgdisplacement = displacement.stream().mapToDouble(p -> p.doubleValue()).average().getAsDouble(); 
			Double avgnormalizedMaxLoad = normalizedMaxLoad.stream().mapToDouble(p -> p.doubleValue()).average().getAsDouble();
			Double avgtime = totalTime.stream().mapToDouble(p -> p.longValue()).average().getAsDouble();
			Long mintime = totalTime.stream().mapToLong(p -> p.longValue()).min().getAsLong();
			Long maxtime = totalTime.stream().mapToLong(p -> p.longValue()).max().getAsLong();
			String[] metrics = {
					graphName,						//graph name
					totalNodes.toString(), 			//total nodes
					totalEdges.toString(), 			//total	edges
					ord,		//gl type
					k.toString(),					//k
					heuristic.getHeuristicName().replace(' ', '_'),  //heuristic name
					avgdisplacement.toString(), 		//displacement
					avgcuttedEdges.toString(),			//cutted edges
					maxheuristicEdgesRatio.toString(),
					minheuristicEdgesRatio.toString(),
					avgheuristicEdgesRatio.toString(),	//edges ratio
					maxtime.toString(),
					mintime.toString(),
					avgtime.toString(),
					ITERATION_TIME.toString()			//iteration time
			};
			saveCSV(metrics);
			log.info("Test for " + HeuristicFactory.getHeuristic(i,par).getHeuristicName() + " done.");
		}
		
	}


	private void myAssertEquals(long intValue, long totalPartitionedEdges) {
		assertEquals(intValue, totalPartitionedEdges);
	}


	
}
