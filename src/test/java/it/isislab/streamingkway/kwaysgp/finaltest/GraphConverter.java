package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class GraphConverter extends TestCase {
	
	private static final String FOLDER = "resources/social";
	
	public void testConverter() throws IOException {
		File ff = new File(FOLDER);
		for(File f: ff.listFiles(p -> !p.getName().startsWith("CA-"))) {
			if (!f.getName().endsWith(".txt")) continue;
			System.out.println("Test for: " + f.getName());
			String name = f.getName();
			FileInputStream fis = new FileInputStream(f);
			FileOutputStream fos = new FileOutputStream(new File(name.substring(0, name.length() - ".txt".length()) + ".graph"));
			Converter c = new Converter(fis, fos, " ");
			c.runConversion();
		}
		for(File f: ff.listFiles(p -> p.getName().startsWith("CA-"))) {
			if (!f.getName().endsWith(".txt")) continue;
			System.out.println("Test for: " + f.getName());
			String name = f.getName();
			FileInputStream fis = new FileInputStream(f);
			FileOutputStream fos = new FileOutputStream(new File(name.substring(0, name.length() - ".txt".length()) + ".graph"));
			Converter c = new Converter(fis, fos, "\t");
			c.runConversion();
		}
	}
	
}
