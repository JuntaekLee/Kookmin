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

	// ���ӵ� ������
	private MotionClassifier mAccelCollector;

	// ���� �Ŵ���
	private SensorManager mSm;

	public BackgroundCollector() {
		super();
		Log.d(TAG, "BackgroundCollector() ȣ��");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand ȣ��");

		// ����
		mAccelCollector = new MotionClassifier();
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);

		// ���� ���
		mSm.registerListener(mAccelCollector,
				mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				MotionOption.SENSOR_DELAY);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "BackgroundCollector onDestroy ȣ��");
		// ���� ����
		mSm.unregisterListener(mAccelCollector);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
