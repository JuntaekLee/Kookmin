package kr.ac.kookmin.cs.motion;

import java.util.ArrayList;

public class CharacterExtractor {
	public static final int LPC_COEFFICIENT_SIZE = 8;

	public static double standardDeviation(ArrayList<Double> array, int option) {
		int size = array.size();
		if (size < 2)
			return Double.NaN;
		double sum = 0.0;
		double sd = 0.0;
		double diff;
		double meanValue = average(array);
		for (int i = 0; i < size; i++) {
			diff = array.get(i) - meanValue;
			sum += diff * diff;
		}
		sd = Math.sqrt(sum / (size - option));
		return sd;
	}

	public static double averageDeviation(ArrayList<Double> array) {
		int size = array.size();
		if (size < 2)
			return Double.NaN;
		double sum = 0.0f;
		double sd = 0.0f;
		double diff;
		double meanValue = average(array);
		for (int i = 0; i < size; i++) {
			diff = array.get(i) - meanValue;
			sum += diff * diff;
		}
		sd = sum / size;
		return sd;
	}

	public static double variance(ArrayList<Double> array, int option) {
		int size = array.size();
		if (size < 2)
			return Double.NaN;
		double sum = 0.0f;
		double sd = 0.0f;
		double diff;
		double meanValue = average(array);
		for (int i = 0; i < size; i++) {
			diff = array.get(i) - meanValue;
			sum += diff * diff;
		}
		sd = sum / (size - option);
		return sd;
	}

	public static double sum(ArrayList<Double> array) {
		double sum = 0.0f;
		int size = array.size();
		for (int i = 0; i < size; i++)
			sum += array.get(i);
		return sum;
	}

	public static double average(ArrayList<Double> array) { // 산술 평균 구하기
		float sum = 0.0f;
		int size = array.size();
		for (int i = 0; i < size; i++)
			sum += array.get(i);
		return sum / size;
	}

	public static double rms(ArrayList<Double> array) {
		double ms = 0;
		int size = array.size();
		for (int i = 0; i < size; i++)
			ms += array.get(i) * array.get(i);
		ms /= size;
		return Math.sqrt(ms);
	}

	public static double[] getCoefficients(int p, ArrayList<Double> array) {
		double r[] = new double[p + 1]; // size = 11
		int N = array.size(); // size = 256
		for (int T = 0; T < r.length; T++) {
			for (int t = 0; t < N - T; t++) {
				r[T] += array.get(t) * array.get(t + T);
			}
		}
		double e = r[0];
		double e1 = 0.0f;
		double k = 0.0f;
		double alpha_new[] = new double[p + 1];
		double alpha_old[] = new double[p + 1];
		alpha_new[0] = 1.0f;
		alpha_old[0] = 1.0f;
		for (int h = 1; h <= p; h++) {
			alpha_new[h] = 0.0f;
			alpha_old[h] = 0.0f;
		}
		double sum = 0.0f;
		for (int i = 1; i <= p; i++) {
			sum = 0;
			for (int j = 1; j <= i - 1; j++) {
				sum += alpha_old[j] * (r[i - j]);
			}
			k = ((r[i]) - sum) / e;
			alpha_new[i] = k;
			for (int c = 1; c <= i - 1; c++) {
				alpha_new[c] = alpha_old[c] - (k * alpha_old[i - c]);
			}
			e1 = (1 - (k * k)) * e;
			for (int g = 0; g <= i; g++) {
				alpha_old[g] = alpha_new[g];
			}
			e = e1;
		}
		for (int a = 1; a < alpha_new.length; a++)
			alpha_new[a] = -1 * alpha_new[a];
		return alpha_new;
	}

}
