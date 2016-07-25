package it.isislab.streamingkway.graphloaders.graphtraversingordering;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator<Node> implements Comparator<Node> {
	
	Random rnd = new Random();

	public int compare(Node o1, Node o2) {
		return rnd.nextBoolean() == true ? -rnd.nextInt() : rnd.nextInt();
	}

}
