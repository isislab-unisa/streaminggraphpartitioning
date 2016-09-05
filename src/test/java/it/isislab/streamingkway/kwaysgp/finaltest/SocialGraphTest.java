package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
public class SocialGraphTest 
extends TestCase implements HeuristicsTest
{


	public static final Double MES_TOLERANCE = 0.06;
	public static final String PLACEHOLDER_B = "B";
	public static final String PLACEHOLDER_D = "D";
	public static final String PLACEHOLDER_R = "R";
	private static final String CSV_SUFFIX = "-res.csv";
	private static final int MAX_PARTITION_SIZE = 128;
	private static final String FOLDER = "resources/";
	private static final String CSV_FOLDER ="/csv/";
	private static final String OUTPUT_FILE = "toremove.file";
	private static final String SOCIAL_FOLDER ="resources/socialGr/";
	private Logger log = Logger.getGlobal();
	private CSVWriter writer;
	/** 
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @throws IOException 
	 */
	public SocialGraphTest( String testName ) throws IOException
	{
		super( testName );
	}


	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( SocialGraphTest.class );
	}


	public void testStreet() throws HeuristicNotFound, IOException, InterruptedException, IllegalArgumentException, IllegalAccessException {
		File fold = new File(SOCIAL_FOLDER);
		//seq
		for (File fpin: fold.listFiles(p -> p.getName().endsWith(".graph"))) {
			String graphName = SOCIAL_FOLDER + fpin.getName();
			
			String[] ords = {".bfs",".dfs",""};
			
			File fpout = new File(SOCIAL_FOLDER + OUTPUT_FILE);
			writer = new CSVWriter(new FileWriter(new File(SOCIAL_FOLDER+ CSV_FOLDER +fpin.getName() + CSV_SUFFIX)),' ');
			Integer C = -1;
			for (int i = 0; i < ITERATION_TIME; i++) {
				File graphNameBfs = new File(graphName + ".bfs." + i);
				File graphNameDfs = new File(graphName + ".dfs." + i);
				
				log.info("Making BSF graph: " + i);
				OrdinatorGraphLoader ogl = new OrdinatorGraphLoader(new FileInputStream(fpin), new FileOutputStream(graphNameBfs),
						new BFSTraversing());
				ogl.runPartition();
				log.info("Making DFS graph: " + i);
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
					socialHeuristicTest(graphFile, fpout, k, C, 
							fpin.getName(), log, true, ord == "" ? "rnd" : ord);
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


	private void socialHeuristicTest(File fpIn, File fpOut, Integer k, Integer C, 
			String graphName, Logger log, boolean par, String ord) throws HeuristicNotFound, IOException, InterruptedException {
		
		ArrayList<Double> heuristicEdgesRatio = null;
		ArrayList<Double> cuttedEdges = null;
		ArrayList<Double> displacement = null;
		ArrayList<Double> normalizedMaxLoad = null;
		ArrayList<Long> totalTime = null;
		ArrayList<Long> partTime = null;
		ArrayList<Long> iotime = null;

		
		ArrayList<Integer> allHeuristics = new ArrayList<Integer>(8);
		allHeuristics.add(RelationshipHeuristics.L_C_NORM_DISPERSION_BASED);
		allHeuristics.add(RelationshipHeuristics.L_C_ABS_DISPERSION_BASED);
		allHeuristics.add(RelationshipHeuristics.L_ABS_DISPERSION_BASED);
		allHeuristics.add(RelationshipHeuristics.E_C_ABS_DISPERSION_BASED);
		
		allHeuristics.add(Heuristic.BALANCE_BIG);
		allHeuristics.add(Heuristic.U_DETERMINISTIC_GREEDY);
		allHeuristics.add(Heuristic.L_DETERMINISTIC_GREEDY);
		allHeuristics.add(Heuristic.E_DETERMINISTIC_GREEDY);
		
		log.info("Testing for k= "+k );
		
		GraphLoader gl = null; 
		ParallelQualityChecker qc = new ParallelQualityChecker();
		SGPHeuristic heuristic = null;
		
		for (Integer i : allHeuristics) {
			heuristicEdgesRatio = new ArrayList<Double>();
			cuttedEdges = new ArrayList<Double>();
			displacement = new ArrayList<Double>();
			normalizedMaxLoad = new ArrayList<Double>();
			totalTime = new ArrayList<Long>();
			partTime = new ArrayList<Long>();
			iotime = new ArrayList<Long>();
			
			Integer totalNodes = 0;
			Integer totalEdges = 0;
			File fpInTest = null;
			for (int j = 0; j < ITERATION_TIME; j++) {
				if (!ord.equals("rnd")) {
					String fname = SOCIAL_FOLDER + fpIn.getName();
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
				Long startTime = System.currentTimeMillis();
				gl.runPartition(); 
				Long endTime = System.currentTimeMillis();
				long time = endTime - startTime;
				//total time
				totalTime.add(time);
				//io & part time
				Long onePartTime = gl.getPartitioningTime();
				Long oneIoTime = time - onePartTime;
				partTime.add(onePartTime);
				iotime.add(oneIoTime);
				//edge ratio
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
				//check normalized maximum load
				normalizedMaxLoad.add(qc.getNormalizedMaximumLoad(gl.getGraphPartitionator().getPartitionMap(), 
						gl.getGraphPartitionator().getGraph()));
			}
			String[] metrics = getMetrics(k, graphName, ord, heuristicEdgesRatio, cuttedEdges, displacement,
					normalizedMaxLoad, totalTime, heuristic, totalNodes, totalEdges, iotime, partTime);
			saveCSV(metrics);
			log.info("Test for " + HeuristicFactory.getHeuristic(i,par).getHeuristicName() + " done.");
		}
		
	}

	/**
	 * @param k
	 * @param graphName
	 * @param ord
	 * @param heuristicEdgesRatio
	 * @param cuttedEdges
	 * @param displacement
	 * @param normalizedMaxLoad
	 * @param totalTime
	 * @param heuristic
	 * @param totalNodes
	 * @param totalEdges
	 * @return
	 */
	private String[] getMetrics(Integer k, String graphName, String ord, ArrayList<Double> heuristicEdgesRatio,
			ArrayList<Double> cuttedEdges, ArrayList<Double> displacement, ArrayList<Double> normalizedMaxLoad,
			ArrayList<Long> totalTime, SGPHeuristic heuristic, Integer totalNodes, Integer totalEdges, 
			ArrayList<Long> iotime, ArrayList<Long> parttime) {
		Double avgheuristicEdgesRatio = getDoubleAverage(heuristicEdgesRatio); 
		Double maxheuristicEdgesRatio = getDoubleMax(heuristicEdgesRatio);
		Double minheuristicEdgesRatio = getDoubleMin(heuristicEdgesRatio);
		Double avgcuttedEdges = getDoubleAverage(cuttedEdges);
		@SuppressWarnings("unused")
		Double avgdisplacement = getDoubleAverage(displacement); 
		Double avgnormalizedMaxLoad = getDoubleAverage(normalizedMaxLoad);
		Double avgtime = totalTime.stream().mapToDouble(p -> p.doubleValue()).average().getAsDouble();
		Long mintime = getLongMin(totalTime);
		Long maxtime = getLongMax(totalTime);
		Long maxiotime = getLongMax(iotime);
		Long miniotime = getLongMin(iotime);
		Double avgiotime = iotime.stream().mapToLong(p -> p.longValue()).average().getAsDouble();
		Long maxparttime = getLongMax(parttime);
		Long minparttime = getLongMax(parttime);
		Double avgparttime = parttime.stream().mapToLong(p -> p.longValue()).average().getAsDouble();
		
		String[] metrics = {
				graphName,						//graph name
				totalNodes.toString(), 			//total nodes
				totalEdges.toString(), 			//total	edges
				ord,		//gl type
				k.toString(),					//k
				heuristic.getHeuristicName().replace(' ', '_'),  //heuristic name
				shortHeuristicName(heuristic.getHeuristicName()),
				avgnormalizedMaxLoad.toString(), 		//displacement
				avgcuttedEdges.toString(),			//cutted edges
				maxheuristicEdgesRatio.toString(),
				minheuristicEdgesRatio.toString(),
				avgheuristicEdgesRatio.toString(),	//edges ratio
				maxtime.toString(),
				mintime.toString(),
				avgtime.toString(),
				maxiotime.toString(),
				miniotime.toString(),
				avgiotime.toString(),
				maxparttime.toString(),
				minparttime.toString(),
				avgparttime.toString(),
				ITERATION_TIME.toString()			//iteration time
		};
		return metrics;
	}


	/**
	 * @param totalTime
	 * @return
	 */
	private long getLongMax(ArrayList<Long> totalTime) {
		return totalTime.stream().mapToLong(p -> p.longValue()).max().getAsLong();
	}


	/**
	 * @param totalTime
	 * @return
	 */
	private long getLongMin(ArrayList<Long> totalTime) {
		return totalTime.stream().mapToLong(p -> p.longValue()).min().getAsLong();
	}


	/**
	 * @param heuristicEdgesRatio
	 * @return
	 */
	private double getDoubleMin(ArrayList<Double> heuristicEdgesRatio) {
		return heuristicEdgesRatio.stream().mapToDouble(p -> p.doubleValue()).min().getAsDouble();
	}


	/**
	 * @param heuristicEdgesRatio
	 * @return
	 */
	private double getDoubleMax(ArrayList<Double> heuristicEdgesRatio) {
		return heuristicEdgesRatio.stream().mapToDouble(p -> p.doubleValue()).max().getAsDouble();
	}


	/**
	 * @param heuristicEdgesRatio
	 * @return
	 */
	private double getDoubleAverage(ArrayList<Double> heuristicEdgesRatio) {
		return heuristicEdgesRatio.stream().mapToDouble(p -> p.doubleValue()).average().getAsDouble();
	}


	private String shortHeuristicName(String heuristicName) {
		String[] shortenHN = heuristicName.split(" ");
		String res = Arrays.stream(shortenHN)
			.map(p -> Character.toString(p.charAt(0)))
			.map(p -> p.toUpperCase())
			.collect(Collectors.joining(""));
		if (res.charAt(res.length() -1 ) == 'P') {
			res = res.substring(0, res.length() -1);
		}
		return res;
	}


	private void myAssertEquals(long intValue, long totalPartitionedEdges) {
		assertEquals(intValue, totalPartitionedEdges);
	}


	
}
