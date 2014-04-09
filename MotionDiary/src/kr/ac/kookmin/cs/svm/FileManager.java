package kr.ac.kookmin.cs.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class FileManager {
	// 기본 폴더 위치 & 파일명
	public static final String SVM_FILE_FOLDER = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/MotionDiary";
	public static final String SVM_TRAIN_MODEL_FILE = SVM_FILE_FOLDER
			+ "/svm_train_model.txt";
	public static final String RESTORE_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/restore_motion_data.txt";
	public static final String MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/motion_data.txt";

	// 학습 Input 파일 생성
	public void makeInputFile(String inputFileName,
			ArrayList<LearningData> data, String label) throws IOException {
		// 초기 파일 생성
		initFile(inputFileName);

		BufferedWriter out = new BufferedWriter(new FileWriter(inputFileName,true));

		int size = data.size();
		for (int i = 0; i < size; i++) {
			// 파일 작성
			out.write(label);
			out.write(" 1:" + data.get(i).LpcCoefficients[1]);
			out.write(" 2:" + data.get(i).LpcCoefficients[2]);
			out.write(" 3:" + data.get(i).LpcCoefficients[3]);
			out.write(" 4:" + data.get(i).LpcCoefficients[4]);
			out.write(" 5:" + data.get(i).LpcCoefficients[5]);
			out.write(" 6:" + data.get(i).LpcCoefficients[6]);
			out.write(" 7:" + data.get(i).LpcCoefficients[7]);
			out.write(" 8:" + data.get(i).Average);
			out.write(" 9:" + data.get(i).AverageDeviation);
			out.write(" 10:" + data.get(i).Rms);
			out.write(" 11:" + data.get(i).StandardDeviation);
			out.write(" 12:" + data.get(i).Sum);
			out.write(" 13:" + data.get(i).Variance);
			out.newLine();
		}

		out.close();

	}

	// 초기 파일 생성
	public static void initFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
	}
}
