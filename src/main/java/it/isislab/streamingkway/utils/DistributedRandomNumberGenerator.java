package it.isislab.streamingkway.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import it.isislab.streamingkway.exceptions.ProbabilityException;

public class DistributedRandomNumberGenerator {

	
    private static final double TOLL = 0.01;
	private static final double N_TOLL = -0.01;
	private Map<Integer, Double> distribution;
    private double distSum;
    private int k;

    public DistributedRandomNumberGenerator(int k) {
    	distribution = new HashMap<>();
    	distSum = Double.MIN_VALUE;
    	this.k = k;
    }
    
    public void setDistribution(Map<Integer, Double> probs) {
		this.distribution = probs;
		distSum = probs.values().parallelStream().mapToDouble(p -> p.doubleValue()).sum();
	}
    
    public void setDistribution(Map<Integer, Double> probs, Double dist) {
    	double total = probs.values().parallelStream().mapToDouble(p -> p.doubleValue()).sum();
    	if (total > 1.0 + TOLL || total < 1.0 + N_TOLL) {
    		throw new ProbabilityException("The sum of the probs must be 1. Actually it is " + total);
    	}
		this.distribution = probs;
		distSum = dist;
	}

    public void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public void removeNumber(int value) {
    	if (distribution.containsKey(value)) {
    		double removedNumber = distribution.remove(value);
    		distSum -= removedNumber;
    	}
    }
    
    public int getDistributedRandomNumber() {
    	if (distribution.isEmpty()) {
    		return ThreadLocalRandom.current().nextInt(k) + 1;
    	}
        double rand = ThreadLocalRandom.current().nextInt();
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return ThreadLocalRandom.current().nextInt(k) + 1;
    }

    public String toString() {
    	return distribution.toString();
    }

	public boolean isEmpty() {
		return distribution.isEmpty();
	}
}