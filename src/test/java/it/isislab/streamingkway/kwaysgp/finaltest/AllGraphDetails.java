package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import au.com.bytecode.opencsv.CSVWriter;
import junit.framework.TestCase;

public class AllGraphDetails extends TestCase{
	
	private static final String FOLDER = "resources/";
	private static final String GRAPH_DETAILS = "graphsdet/graphs1.csv";
	Logger log = Logger.getGlobal();

	public AllGraphDetails() {
	}

	public void testDetails() throws IOException, InterruptedException {
		File fold = new File(FOLDER);
		CSVWriter wr = new CSVWriter(new FileWriter(new File(FOLDER + GRAPH_DETAILS)));
		printHeader(wr);
		for (File fpin: fold.listFiles(p -> p.getName().endsWith("uto.graph"))) { 
			//if (fpin.getName().equals("auto.graph")) continue;
			log.info("Analyzing graph : " + fpin.getName());
			GraphAnalyser ga = new GraphAnalyser(new FileInputStream(fpin), wr, log);
			String grName = fpin.getName().substring(0, fpin.getName().length() - ".graph".length());
			ga.runLoad(grName);
			ga = null;
			System.gc();
			Thread.sleep(400l);
		}
		
		wr.close();
	}

	private void printHeader(CSVWriter wr) throws IOException {
		String[] header = {
			"Nome",
			"Numero di nodi",
			"Numero di archi",
			"Coefficiente di clustering",
			"Densita'",
			"Grado massimo",
			"Grado minimo",
			"Grado medio",
			"Componenti connesse"
		};
		wr.writeNext(header);
		wr.flush();
	}
	
}
