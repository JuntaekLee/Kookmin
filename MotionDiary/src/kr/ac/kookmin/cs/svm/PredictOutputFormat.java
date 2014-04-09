package kr.ac.kookmin.cs.svm;

public class PredictOutputFormat {

	public int[] labels;
	public double[] prob_estimates;
	public double predict;

	public PredictOutputFormat() {
		super();
		labels = null;
		prob_estimates = null;
	}

	public PredictOutputFormat(int[] labels, double[] prob_estimates) {
		super();
		this.labels = labels;
		this.prob_estimates = prob_estimates;
	}

}
