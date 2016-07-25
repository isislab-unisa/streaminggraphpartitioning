package it.isislab.streamingkway.utils;

public class MathUtils {

	public static long binomial(int n, final int k) {
		final int min = (k < n - k ? k : n - k);
		long bin = 1;
		for (int i = 1; i <= min; i++) {
			bin *= n;

			bin /= i;
			n--;
		}
		return bin;
	}

}
