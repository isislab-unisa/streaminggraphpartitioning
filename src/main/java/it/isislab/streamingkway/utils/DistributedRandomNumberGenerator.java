package it.isislab.streamingkway.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedRandomNumberGenerator {

	
    private Map<Integer, Double> distribution;
    private double distSum;

    
    public void setDistribution(Map<Integer, Double> probs) {
		this.distribution = probs;
	}


    public DistributedRandomNumberGenerator() {
        distribution = new HashMap<>();
    }

    public void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public void removeNumber(int value) {
    	double removedNumber = distribution.remove(value);
    	distSum -= removedNumber;
    }
    
    public int getDistributedRandomNumber() {
        double rand = Math.random();
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return new Random().nextInt(distribution.size());
    }

}