package kr.ac.kookmin.cs.motion;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;

public class MotionClassifier implements SensorEventListener {
	public final static String TAG = "AccelDataCollector";
	// 가속도 데이터
	private AccelData mAccelData;

	// 전처리 데이터
	private Queue<Double> mPreprocessedData;


	// 데이터 전처리
	private boolean mIsCheckMeaningful = false;
	private boolean mIsCheckDelay = false;

	// 타임 핸들러
	private Handler mTimeHandler;

	public MotionClassifier() {
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
			// x, y, z 축 이동평균 필터 적용
			mAccelData.x.newNum(v[0]);
			mAccelData.y.newNum(v[1]);
			mAccelData.z.newNum(v[2]);

			// 이동평균 데이터
			float filteredX = mAccelData.x.getAvg();
			float filteredY = mAccelData.y.getAvg();
			float filteredZ = mAccelData.z.getAvg();

			// 유의미 데이터 체크
			if (mIsCheckMeaningful == false && mIsCheckDelay == false) {
				if (DataPreprocessor.checkMeaningfulData(filteredX, filteredY, filteredZ)) {
					Log.d(TAG, "유의미 데이터 발견");
					mIsCheckMeaningful = true;
					mIsCheckDelay = true;
					mTimeHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// 수집된 전처리 데이터 특징 추출
							mIsCheckMeaningful = false;
							
							// 특징 벡터 분류하기
							
							// 데이터 리셋
							resetData();
						}
					}, MotionOption.COLLECT_DATA_TIME);
					mTimeHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mIsCheckDelay = false;
						}
					}, MotionOption.ACCEL_PERIOD_TIME);
				}
			}

			// 데이터 전처리
			if (mIsCheckMeaningful == true) {
				Log.d(TAG, "데이터 전처리...");
				mPreprocessedData.add(DataPreprocessor.proprocessData(filteredX, filteredY,
						filteredZ));
			}

			break;

		}
	}
	
	// 데이터 리셋
	private void resetData(){
		mPreprocessedData.clear();
		mAccelData.x.clear();
		mAccelData.y.clear();
		mAccelData.z.clear();
		
	}


}
