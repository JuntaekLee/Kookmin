package kr.ac.kookmin.cs.motion;

import android.hardware.SensorManager;

public class MotionOption {

	public static final double PREPROCESSING_CRITICAL_VALUE = 10.0;
	public static final int ACCEL_PERIOD_TIME = 12000;
	public static final int COLLECT_DATA_TIME = 3000;
	public static final int LEARN_DELAY_TIME = 1000;
	public static final int LEARN_DATA_TIME = 1500;
	public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI;
	public static final int MOVING_FILTER_PERIOD = 5;
}
