package it.isislab.streamingkway.kwaysgp;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParallelCollectionTest extends TestCase {

	public ParallelCollectionTest(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		return new TestSuite(ParallelCollectionTest.class);
	}	
	
	public void testOnMap() {
		Map<Integer, Integer> maxMap = new HashMap<>();
		maxMap.put(1, 10);
		maxMap.put(2, 5);
		maxMap.put(3, 59);
		maxMap.put(4, 2);
		Long startTime = System.currentTimeMillis();
		Integer maxIndex = maxMap.entrySet().parallelStream()
				.max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
				.get().getKey();
		Long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		System.out.println(maxIndex);
		assertEquals(maxIndex.intValue(), 3);
	}
	
	
	
}
