package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import it.isislab.streamingkway.graphloaders.GraphLoader;
import it.isislab.streamingkway.graphloaders.SimpleGraphLoader;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.BFSTraversing;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.DFSTraversing;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.factory.HeuristicFactory;
import it.isislab.streamingkway.metrics.ParallelQualityChecker;
import it.isislab.streamingkway.metrics.QualityChecker;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String[] graphs={"4elt.graph","pl1000.graph","paperdispersion.graph","facebook_combined.graph","CA-HepTh.graph","CA-GrQc.graph","CA-AstroPh.graph","CA-HepTh.graph",};
		HashMap<String, Integer> edges=new HashMap<String,Integer>();
		SGPHeuristic hdipsersion = HeuristicFactory.getHeuristic(35, false);
		SGPHeuristic ldg = HeuristicFactory.getHeuristic(5, false);
		QualityChecker qc = new ParallelQualityChecker();
		int K=4;
		int ITE=5;
		
		for(int j=0;j< graphs.length;j++)
		{
			
			String graph=graphs[j];
			System.out.print("Graph: "+graph+"->" );
			File fgraph=new File("resources/"+graph);
			double dI=0;
			double ldgI=0;
			
			for (int i = 0; i < ITE; i++) {
				System.out.print(i+" .. ");
				
				
				File graphNameBfs = new File("resources/"+graph + ".bfs." + i);
				File graphNameDfs = new File("resources/"+graph + ".dfs." + i);
				File graphNameRnd = new File("resources/"+graph + ".rnd." + i);

				OrdinatorGraphLoader ogl = new OrdinatorGraphLoader(new FileInputStream(fgraph), new FileOutputStream(graphNameBfs),
						new BFSTraversing());
				ogl.runPartition();
				OrdinatorGraphLoader ogld = new OrdinatorGraphLoader(new FileInputStream(fgraph), new FileOutputStream(graphNameDfs),
						new DFSTraversing());
				ogld.runPartition();

				RandomOrdinator ordinatorGraphLoader = new RandomOrdinator(new FileInputStream(fgraph), new FileOutputStream(graphNameRnd), null);
				ordinatorGraphLoader.runPartition();
				
				GraphLoader gd = new SimpleGraphLoader(new FileInputStream(graphNameRnd), new FileOutputStream(graph+".result"), K,hdipsersion, -1,false);
				gd.runPartition();
				edges.put(graph,gd.getEdgeNumbers());
				
				
				dI+= qc.getCuttingEdgesCount(gd.getGraphPartitionator().getGraph());
				
				
				GraphLoader lgd = new SimpleGraphLoader(new FileInputStream(graphNameRnd), new FileOutputStream(graph+".result"), K,ldg, -1,false);
				lgd.runPartition();
				
				ldgI+= qc.getCuttingEdgesCount(lgd.getGraphPartitionator().getGraph());
				
				
			}
			System.out.println("end.");
			System.out.println("\tK="+K+" db: "+ dI/ITE + " " +edges.get(graph)+ " "  +  dI/(ITE*edges.get(graph)) );
			System.out.println("\tK="+K+" ldg: "+ ldgI/ITE + " " +edges.get(graph)+ " " + ldgI/(ITE*edges.get(graph)));
		}
	}


}
