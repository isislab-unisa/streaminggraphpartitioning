package it.isislab.streamingkway.graphloaders.graphtraversingordering;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator<Node> implements Comparator<Node> {
	
	Random rnd = new Random();

	public int compare(Node o1, Node o2) {
		int random = rnd.nextInt(2);
		return random == 2? -1 : random;
	}

}
