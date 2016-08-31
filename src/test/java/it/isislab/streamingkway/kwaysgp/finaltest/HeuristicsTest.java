package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.heuristics.Heuristic;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.heuristics.relationship.RelationshipHeuristics;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;
import junit.framework.Test;

public interface HeuristicsTest extends Test {

	Integer ITERATION_TIME = 4;
	Long CPU_REFRESH_TIME = 0l;
	String[] HEADER =  {
			"GraphName",
			"TotalNodes", 	
			"TotalEdges",
			"OrderingType",
			"K",
			"CompleteHeuristicName",  
			"HeuristicName",
			"NormalizedLoad", 	
			"CuttedEdges",
			"MaxCuttedEdgesRatio",
			"MinCuttedEdgesRatio",
			"CuttedEdgesRatio",
			"MaxTime",
			"MinTime",
			"AvgTime",
			"MaxIOTime",
			"MinIOTime",
			"AvgIOTime",
			"MaxPartitioningTime",
			"MinPartitioningTime",
			"AvgPartitioningTime",
			"IterationTime"
	};

	default void allHeuristicsTestCompare(File fpIn, File fpOut, Integer k, Integer C, String glType, 
			String graphName, Logger log, boolean par) throws HeuristicNotFound, IOException, InterruptedException {
		Double heuristicEdgesRatio = 0.0;
		Double cuttedEdges = 0.0;
		Double displacement = 0.0;
		Double normalizedMaxLoad = 0.0;
		Long totalTime = 0l;
		GraphLoader gl = null; 

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
		QualityChecker qc = new ParallelQualityChecker();
		
		for (Integer i : allHeuristics) {
			cuttedEdges = 0.0;
			displacement = 0.0;
			totalTime = 0l;
			normalizedMaxLoad = 0.0;
			heuristicEdgesRatio = 0.0; //init entry

			log.info("Executing: " + HeuristicFactory.getHeuristic(i,par).getHeuristicName());
			Integer totalNodes = 0;
			Integer totalEdges = 0;
			SGPHeuristic heuristic = null;
			double max = Double.MIN_VALUE;
			double min =Double.MAX_VALUE;
			long maxTime = Long.MIN_VALUE;
			long minTime = Long.MAX_VALUE;
			for (int j = 0; j < ITERATION_TIME; j++) {
					
				heuristic = HeuristicFactory.getHeuristic(i,par);
				gl = getGraphLoader(glType, fpIn,fpOut,k,heuristic,C,false);
				Thread.sleep(CPU_REFRESH_TIME);
				Long startTime = System.currentTimeMillis();
				gl.runPartition(); 
				Long endTime = System.currentTimeMillis();
				long time = endTime - startTime;
				totalTime += time;
				if (maxTime <= time) {
					maxTime = time;
				}
				if (minTime >= time) {
					minTime = time;
				}
				heuristicEdgesRatio += qc.getCuttingEdgeRatio(gl.getGraphPartitionator().getGraph());
				if (max <= heuristicEdgesRatio) {
					max = heuristicEdgesRatio;
				}
				if (min >= heuristicEdgesRatio) {
					min = heuristicEdgesRatio;
				}
				cuttedEdges += qc.getCuttingEdgesCount(gl.getGraphPartitionator().getGraph());
				//count total partitioned nodes
				totalNodes = gl.getGraphPartitionator().getTotalPartitionedNodes();
				myAssertEquals(totalNodes.intValue(), gl.getNodeNumbers());
				myAssertEquals(totalNodes.intValue(),gl.getGraphPartitionator().getGraph().getNodeCount());
				//count total partitioned edges
				totalEdges = gl.getEdgeNumbers();
				myAssertEquals(totalEdges.intValue(), gl.getGraphPartitionator().getGraph().getEdgeCount());
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
					heuristic.getHeuristicName().replace(' ', '_'),  //heuristic name
					displacement.toString(), 		//displacement
					cuttedEdges.toString(),			//cutted edges
					Double.toString(max),			//max edges ratio
					Double.toString(min),			//min edges ratio
					heuristicEdgesRatio.toString(),	//edges ratio
					Long.toString(maxTime),			//max time
					Long.toString(minTime),			//min time
					Long.toString(totalTime/ITERATION_TIME), //time
					ITERATION_TIME.toString()			//iteration time
			};
			log.info("Metrics: " + metrics);
			saveCSV(metrics);
			log.info("Test for " + HeuristicFactory.getHeuristic(i,par).getHeuristicName() + " done.");
		}
		
	}
	
	void myAssertEquals(int intValue,int nodeNumbers);
	void saveCSV(String[] metrics) throws IOException;
	GraphLoader getGraphLoader(String glType,File fpIn,File fpOut,Integer k,SGPHeuristic heuristic,Integer c,boolean b) throws IOException;
	
}
