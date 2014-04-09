package kr.ac.kookmin.cs.svm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;

class SVMPredict {
	private static svm_print_interface svm_print_null = new svm_print_interface() {
		public void print(String s) {
		}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface() {
		public void print(String s) {
			System.out.print(s);
		}
	};

	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) {
		svm_print_string.print(s);
	}

	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private static PredictOutputFormat predict(BufferedReader input,
			svm_model model, int predict_probability) throws IOException {
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type = svm.svm_get_svm_type(model);
		int nr_class = svm.svm_get_nr_class(model);
		double[] prob_estimates = null;

		// Ãß°¡
		PredictOutputFormat result;
		if (svm_type == svm_parameter.ONE_CLASS) {
			result = new PredictOutputFormat();
		} else {
			result = new PredictOutputFormat(new int[nr_class],
					new double[nr_class]);
		}

		if (predict_probability == 1) {
			if (svm_type == svm_parameter.EPSILON_SVR
					|| svm_type == svm_parameter.NU_SVR) {
				SVMPredict
						.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
								+ svm.svm_get_svr_probability(model) + "\n");
			} else {
				int[] labels = new int[nr_class];
				svm.svm_get_labels(model, labels);
				prob_estimates = new double[nr_class];
				result.labels = labels;
			}
		}
		while (true) {
			String line = input.readLine();
			if (line == null)
				break;

			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens() / 2;
			svm_node[] x = new svm_node[m];
			for (int j = 0; j < m; j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability == 1
					&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
				v = svm.svm_predict_probability(model, x, prob_estimates);
				result.predict = v;
				for (int j = 0; j < nr_class; j++)
					result.prob_estimates[j] = prob_estimates[j];
			} else {
				v = svm.svm_predict(model, x);
				result.predict = v;
			}

			if (v == target)
				++correct;
			error += (v - target) * (v - target);
			sumv += v;
			sumy += target;
			sumvv += v * v;
			sumyy += target * target;
			sumvy += v * target;
			++total;
		}
		if (svm_type == svm_parameter.EPSILON_SVR
				|| svm_type == svm_parameter.NU_SVR) {
			SVMPredict.info("Mean squared error = " + error / total
					+ " (regression)\n");
			SVMPredict.info("Squared correlation coefficient = "
					+ ((total * sumvy - sumv * sumy) * (total * sumvy - sumv
							* sumy))
					/ ((total * sumvv - sumv * sumv) * (total * sumyy - sumy
							* sumy)) + " (regression)\n");
		} else
			SVMPredict.info("Accuracy = " + (double) correct / total * 100
					+ "% (" + correct + "/" + total + ") (classification)\n");

		return result;
	}

	private static void exit_with_help() {
		System.err
				.print("usage: svm_predict [options] test_file model_file output_file\n"
						+ "options:\n"
						+ "-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
						+ "-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public PredictOutputFormat run(String inputFile, String modelFile,
			String option[]) throws IOException {
		int i, predict_probability = 0;
		svm_print_string = svm_print_stdout;

		// parse options
		int optionLen = option.length;
		for (i = 0; i < optionLen; i++) {
			if (option[i].charAt(0) != '-')
				break;
			++i;
			switch (option[i - 1].charAt(1)) {
			case 'b':
				predict_probability = atoi(option[i]);
				break;
			case 'q':
				svm_print_string = svm_print_null;
				i--;
				break;
			default:
				System.err.print("Unknown option: " + option[i - 1] + "\n");
				exit_with_help();
			}
		}
		if (i >= optionLen - 2)
			exit_with_help();
		try {
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			svm_model model = svm.svm_load_model(modelFile);
			if (predict_probability == 1) {
				if (svm.svm_check_probability_model(model) == 0) {
					System.err
							.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			} else {
				if (svm.svm_check_probability_model(model) != 0) {
					SVMPredict
							.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			input.close();
			return predict(input, model, predict_probability);
		} catch (FileNotFoundException e) {
			exit_with_help();
		} catch (ArrayIndexOutOfBoundsException e) {
			exit_with_help();
		}

		return null;
	}
}
