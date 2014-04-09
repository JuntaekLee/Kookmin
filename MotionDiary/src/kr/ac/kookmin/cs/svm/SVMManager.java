package kr.ac.kookmin.cs.svm;

import java.io.IOException;

import android.os.Environment;

public class SVMManager {
	// 옵션
	public static final String SVM_TRAIN_OPTION[] = { "-s", "0", "-t", "2",
			"-n", "0.5", "-g", "0.1", "-p", "0.1", "-h", "1" };
	public static final String SVM_SCALE_OPTION[] = {};
	public static final String SVM_CLASSIFICATION_OPTION[] = { "-b", "0" };

	// 라벨
	public static final String LABEL_WALKING = "1";
	public static final String LABEL_RUNNING = "2";
	
	// 학습, 스케일, 분류
	private SVMTrain mSVMTrainer;
	private SVMScale mSVMScaler;
	private SVMPredict mSVMPredictor;

	public SVMManager() {
		mSVMTrainer = new SVMTrain();
		mSVMScaler = new SVMScale();
		mSVMPredictor = new SVMPredict();
	}

	public void train(String inputFile, String outputFile) throws IOException {
		// 초기 파일 생성
		FileManager.initFile(inputFile);
		FileManager.initFile(outputFile);

		// SVM Train
		mSVMTrainer.run(inputFile, outputFile, SVM_TRAIN_OPTION);
	}

	public void scale(String inputFile, String restoreFile, String outputFile)
			throws IOException {
		// 초기 파일 생성
		if (restoreFile == null)
			FileManager.initFile(outputFile);
		else {
			FileManager.initFile(restoreFile);
		}

		// SVM Scale
		mSVMScaler.run(inputFile, restoreFile, outputFile, SVM_SCALE_OPTION);
	}

	public PredictOutputFormat predict(String inputFile, String modelFile)
			throws IOException {
		PredictOutputFormat result = mSVMPredictor.run(inputFile, modelFile,
				SVM_SCALE_OPTION);
		if (result == null)
			throw new IOException();
		return result;
	}
}
