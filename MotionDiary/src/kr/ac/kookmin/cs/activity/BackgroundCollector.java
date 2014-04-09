package kr.ac.kookmin.cs.activity;

import kr.ac.kookmin.cs.motion.MotionClassifier;
import kr.ac.kookmin.cs.motion.MotionOption;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class BackgroundCollector extends Service {
	public final static String TAG = "BackgroundCollector";

	// 가속도 수집기
	private MotionClassifier mAccelCollector;

	// 센서 매니저
	private SensorManager mSm;

	public BackgroundCollector() {
		super();
		Log.d(TAG, "BackgroundCollector() 호출");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand 호출");

		// 생성
		mAccelCollector = new MotionClassifier();
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);

		// 센서 등록
		mSm.registerListener(mAccelCollector,
				mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				MotionOption.SENSOR_DELAY);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "BackgroundCollector onDestroy 호출");
		// 센서 해제
		mSm.unregisterListener(mAccelCollector);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
