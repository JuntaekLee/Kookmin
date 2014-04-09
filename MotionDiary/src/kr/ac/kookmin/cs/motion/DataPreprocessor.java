package kr.ac.kookmin.cs.motion;

public class DataPreprocessor {
	// 벡터 전처리 함수
	public static double proprocessData(float x, float y, float z) {
		double result = 0.0f;
		result = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

		return result;
	}

	// 유의미 데이터 체크
	public static boolean checkMeaningfulData(float x, float y, float z) {
		double result = 0.0f;
		result = proprocessData(x, y, z);
		if (result > MotionOption.PREPROCESSING_CRITICAL_VALUE)
			return true;
		else
			return false;
	}
}
