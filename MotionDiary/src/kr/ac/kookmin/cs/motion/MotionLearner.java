package kr.ac.kookmin.cs.motion;

import java.util.LinkedList;
import java.util.Queue;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;

public class MotionLearner implements SensorEventListener {
	public final static String TAG = "MotionLearner";
	// 가속도 데이터
	private AccelData mAccelData;

	// 전처리 데이터
	public Queue<Double> mPreprocessedData;

	// 타임 핸들러
	private Handler mTimeHandler;

	public MotionLearner() {
		mTimeHandler = new Handler();
		mAccelData = new AccelData(MotionOption.MOVING_FILTER_PERIOD);
		mPreprocessedData = new LinkedList<Double>();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] v = event.values;

		switch (event.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:

			// Log.d(TAG, "MotionLearner onSensorChanged 호출");
			// x, y, z 축 이동평균 필터 적용
			mAccelData.x.newNum(v[0]);
			mAccelData.y.newNum(v[1]);
			mAccelData.z.newNum(v[2]);

			// 이동평균 데이터
			float filteredX = mAccelData.x.getAvg();
			float filteredY = mAccelData.y.getAvg();
			float filteredZ = mAccelData.z.getAvg();

			// 데이터 전처리
			try {
				mPreprocessedData.add(DataPreprocessor.proprocessData(
						filteredX, filteredY, filteredZ));
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		}
	}

	// 데이터 리셋
	public void resetData() {
		mPreprocessedData.clear();
		mAccelData.x.clear();
		mAccelData.y.clear();
		mAccelData.z.clear();

	}
}
