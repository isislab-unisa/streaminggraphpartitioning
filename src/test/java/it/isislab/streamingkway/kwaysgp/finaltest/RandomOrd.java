package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

public class RandomOrd {

	@Test
	public void test() throws IOException {
		for (int i = 0; i < 5; i++) {
			FileInputStream fis = new FileInputStream("resources/facebook_combined.graph");
			FileOutputStream fos = new FileOutputStream("resources/facebook_combined.graph.rnd."+ i);
			OrdinatorGraphLoader ordinatorGraphLoader = new OrdinatorGraphLoader(fis, fos, null);
			ordinatorGraphLoader.runPartition();
			
		}
	}

}
