package kr.ac.kookmin.cs.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import kr.ac.kookmin.cs.motion.CharacterExtractor;
import kr.ac.kookmin.cs.motion.MotionLearner;
import kr.ac.kookmin.cs.motion.MotionOption;
import kr.ac.kookmin.cs.svm.FileManager;
import kr.ac.kookmin.cs.svm.LearningData;
import kr.ac.kookmin.cs.svm.SVMManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LearningMotionActivity extends Activity {
	public final static String TAG = "LearningMotionActivity";

	// ��
	private Button mLearningCompleteBtn;
	private Button mLearningWalkingBtn;
	private Button mLearningRunningBtn;

	// ����
	private MotionLearner mMotionLearner;
	private SensorManager mSm;

	// �н�������
	private ArrayList<LearningData> mLearningData;

	// SVM �Ŵ���
	private SVMManager mSVMManger;

	// ���� �Ŵ���
	private FileManager mFileManager;

	// �н� ���� �÷���
	private boolean mIsWalkingCompleted = false;
	private boolean mIsRunningCompleted = false;

	// ���α׷��� ���̾�α�
	private ProgressDialog mMotionProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning_motion);

		// ����
		mLearningWalkingBtn = (Button) findViewById(R.id.learning_walking_motion_btn);
		mLearningRunningBtn = (Button) findViewById(R.id.learning_running_motion_btn);
		mLearningCompleteBtn = (Button) findViewById(R.id.learning_complete_btn);
		mMotionLearner = new MotionLearner();
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mLearningData = new ArrayList<LearningData>();
		mSVMManger = new SVMManager();
		mFileManager = new FileManager();
		mMotionProgress = new ProgressDialog(this);

		// Ŭ�������� ����
		mLearningWalkingBtn.setOnClickListener(mClickListener);
		mLearningCompleteBtn.setOnClickListener(mClickListener);
		mLearningRunningBtn.setOnClickListener(mClickListener);

		// ���α׷��� ����
		mMotionProgress.setTitle("�н� ������");
		mMotionProgress.setMessage("�н��� ������ �Դϴ�.");
		mMotionProgress.setIndeterminate(false);
		mMotionProgress.setCancelable(false);

	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.learning_walking_motion_btn:

				if (mIsWalkingCompleted == false) {
					// ���α׷��� ȣ��
					mMotionProgress.show();
					// �Ϸ��ư ������
					mLearningCompleteBtn.setVisibility(View.INVISIBLE);

					// Ư¡ ����
					handleData(mLearningData, FileManager.MOTION_DATA_FILE,
							SVMManager.LABEL_WALKING);
				} else {
					Toast.makeText(getApplicationContext(),
							StringMessage.LEARNING_WALKING_COMPLETED,
							Toast.LENGTH_LONG).show();
				}

				break;

			case R.id.learning_running_motion_btn:

				if (mIsWalkingCompleted == false) {
					Toast.makeText(getApplicationContext(), "�ȱ� �н��� ���� ���ּ���.",
							Toast.LENGTH_LONG).show();
					break;
				}
				// ���α׷��� ȣ��
				mMotionProgress.show();
				if (mIsRunningCompleted == false) {
					// �Ϸ��ư ������
					mLearningCompleteBtn.setVisibility(View.INVISIBLE);

					// Ư¡ ����
					handleData(mLearningData, FileManager.MOTION_DATA_FILE,
							SVMManager.LABEL_RUNNING);
				} else {
					Toast.makeText(getApplicationContext(),
							StringMessage.LEARNING_RUNNING_COMPLETED,
							Toast.LENGTH_LONG).show();
				}

				break;

			case R.id.learning_complete_btn:
				// ������ ���� ���� ����
				try {
					mSVMManger.scale(FileManager.MOTION_DATA_FILE, null,
							FileManager.RESTORE_MOTION_DATA_FILE);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							StringMessage.IOEXCEPTION_STRING, Toast.LENGTH_LONG)
							.show();
				}

				// �н��� ����
				try {
					mSVMManger.scale(FileManager.MOTION_DATA_FILE,
							FileManager.RESTORE_MOTION_DATA_FILE, null);
					mSVMManger.train(FileManager.MOTION_DATA_FILE,
							FileManager.SVM_TRAIN_MODEL_FILE);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							StringMessage.IOEXCEPTION_STRING, Toast.LENGTH_LONG)
							.show();
				}

				// �н� �Ϸ� �佺Ʈ
				Toast.makeText(getApplicationContext(), "�н��� �Ϸ��Ͽ����ϴ�.",
						Toast.LENGTH_LONG).show();

				break;
			}

		}
	};

	// �ڵ鸵
	private void handleData(final ArrayList<LearningData> learningData,
			final String fileName, final String label) {
		// 3�� �� 3�ʰ� ������ ����
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mSm.registerListener(mMotionLearner,
						mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						MotionOption.SENSOR_DELAY);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// ���� ����
						mSm.unregisterListener(mMotionLearner);

						// Ư¡ ����
						separateData(learningData,
								mMotionLearner.mPreprocessedData);

						// ���� ����
						try {
							mFileManager.makeInputFile(fileName, learningData,
									label);
						} catch (IOException e1) {
							e1.printStackTrace();
							Toast.makeText(getApplicationContext(),
									StringMessage.IOEXCEPTION_STRING,
									Toast.LENGTH_LONG).show();
						}

						// ������ ����
						mMotionLearner.resetData();

						// �н��Ϸ�
						if (label == SVMManager.LABEL_WALKING) {
							Toast.makeText(getApplicationContext(),
									"�ȱ� �н��� �Ϸ��Ͽ����ϴ�.", Toast.LENGTH_LONG)
									.show();
							// �Ϸ� �÷���
							mIsWalkingCompleted = true;
						} else if (label == SVMManager.LABEL_RUNNING) {
							Toast.makeText(getApplicationContext(),
									"�ٱ� �н��� �Ϸ��Ͽ����ϴ�.", Toast.LENGTH_LONG)
									.show();
							// �Ϸ� �÷���
							mIsRunningCompleted = true;
						}

						// �Ϸ� ��ư �����
						if (mIsRunningCompleted == true
								&& mIsWalkingCompleted == true)
							mLearningCompleteBtn.setVisibility(View.VISIBLE);

						// ���α׷��� ����
						mMotionProgress.dismiss();
					}
				}, MotionOption.LEARN_DATA_TIME);
			}
		}, MotionOption.LEARN_DELAY_TIME);
	}

	// Ư¡ ����
	private void extractCharacter(ArrayList<LearningData> learingData,
			ArrayList<Double> rawData) {

		// Ư¡����
		LearningData temp = new LearningData();
		temp.Average = CharacterExtractor.average(rawData);
		temp.AverageDeviation = CharacterExtractor.averageDeviation(rawData);
		temp.LpcCoefficients = CharacterExtractor.getCoefficients(
				CharacterExtractor.LPC_COEFFICIENT_SIZE, rawData);
		temp.Rms = CharacterExtractor.rms(rawData);
		temp.StandardDeviation = CharacterExtractor.standardDeviation(rawData,
				0);
		temp.Sum = CharacterExtractor.sum(rawData);
		temp.Variance = CharacterExtractor.variance(rawData, 0);

		// �н� �����Ϳ� �߰�
		learingData.add(temp);

	}

	// ������ ���������͸� �и��Ѵ�.
	private void separateData(ArrayList<LearningData> learingData,
			Queue<Double> data) {
		ArrayList<Double> temp = new ArrayList<Double>();
		int size = data.size() / 10;

		while (data.isEmpty() == false) {
			try {
				temp.add(data.remove());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (temp.size() == size) {
				extractCharacter(learingData, temp);
				temp.clear();
			}

		}

	}

}
