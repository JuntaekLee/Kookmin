package kr.ac.kookmin.cs.motion;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverageFilter {
	private final Queue<Float> window = new LinkedList<Float>();
	private final int period;
	private float sum;

	public MovingAverageFilter(int period) {

		this.period = period;
	}

	public void newNum(float num) {
		sum += num;
		window.add(num);
		if (window.size() > period) {
			sum -= window.remove();
		}
	}

	public float getAvg() {
		if (window.isEmpty())
			return 0; // technically the average is undefined
		return sum / window.size();
	}

	public void clear() {
		sum = 0.0f;
		window.clear();
	}

}
