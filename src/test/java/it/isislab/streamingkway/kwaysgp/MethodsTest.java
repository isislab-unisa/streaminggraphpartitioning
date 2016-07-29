package it.isislab.streamingkway.kwaysgp;

import java.util.ArrayList;

import it.isislab.streamingkway.utils.DistributedRandomNumberGenerator;
import it.isislab.streamingkway.utils.MathUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MethodsTest extends TestCase {

	private static final int PROB_TOLERANCE = 5;

	public MethodsTest (String testName) {
		super(testName);
	}

	public static Test suite()
	{
		return new TestSuite( MethodsTest.class );
	}

	/**
	 * Binomial Coefficient Test
	 */
	public void testBinomialCoefficient()
	{
		//normal case
		int n = 5; int k = 3; //result must to be 10
		assertEquals(10, MathUtils.binomial(n, k));

		//k = 1 case
		n = 5; k = 1;
		assertEquals(n, MathUtils.binomial(n, k));

		//k = 0 case
		n = 5; k = 0;
		assertEquals(1,MathUtils.binomial(n, k));


		//k = n-1 case
		n = 5; k = n-1;
		assertEquals(n,MathUtils.binomial(n, k));

	}
	
	public void testProbability() {
		DistributedRandomNumberGenerator rand = new DistributedRandomNumberGenerator();
		rand.addNumber(1, 1.0);
		rand.addNumber(2, 0.0);
		assertEquals(rand.getDistributedRandomNumber(), 1);
	}
	
	public void testProbability2() {
		DistributedRandomNumberGenerator rand = new DistributedRandomNumberGenerator();
		rand.addNumber(1, 0.5);
		rand.addNumber(2, 0.5);
		ArrayList<Integer> integers = new ArrayList<Integer>(100);
		for (int i = 0; i < 100; i++) {
			integers.add(rand.getDistributedRandomNumber());
		}
		Long count1 = integers.parallelStream().filter(p -> p.intValue() == 1).count();
		Long count2 = integers.parallelStream().filter(p -> p.intValue() == 2).count();

		assertTrue(Long.compare(50L+PROB_TOLERANCE, count1) >= 0);
		assertTrue(Long.compare(50L-PROB_TOLERANCE, count1) <= 0);
		assertTrue(Long.compare(50L+PROB_TOLERANCE, count2) >= 0);
		assertTrue(Long.compare(50L-PROB_TOLERANCE, count2) <= 0);
		
	}
	
	public void testProbability3() {
		DistributedRandomNumberGenerator rand = new DistributedRandomNumberGenerator();
		rand.addNumber(1, 0.7);
		rand.addNumber(2, 0.3);
		ArrayList<Integer> integers = new ArrayList<Integer>(100);
		for (int i = 0; i < 100; i++) {
			integers.add(rand.getDistributedRandomNumber());
		}
		Long count1 = integers.parallelStream().filter(p -> p.intValue() == 1).count();
		Long count2 = integers.parallelStream().filter(p -> p.intValue() == 2).count();


		
		assertTrue(Long.compare(70L+PROB_TOLERANCE, count1) >= 0);
		assertTrue(Long.compare(70L-PROB_TOLERANCE, count1) <= 0);
		assertTrue(Long.compare(30L+PROB_TOLERANCE, count2) >= 0);
		assertTrue(Long.compare(30L-PROB_TOLERANCE, count2) <= 0);
		
	}
	
}
