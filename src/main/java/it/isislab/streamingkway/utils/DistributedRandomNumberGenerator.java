package it.isislab.streamingkway.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedRandomNumberGenerator {

	
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
    		return new Random().nextInt(k) + 1;
    	}
        double rand = Math.random();
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return new Random().nextInt(k) + 1;
    }

    public String toString() {
    	return distribution.toString();
    }

	public boolean isEmpty() {
		return distribution.isEmpty();
	}
}