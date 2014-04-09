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

	// 뷰
	private Button mLearningCompleteBtn;
	private Button mLearningWalkingBtn;
	private Button mLearningRunningBtn;

	// 센서
	private MotionLearner mMotionLearner;
	private SensorManager mSm;

	// 학습데이터
	private ArrayList<LearningData> mLearningData;

	// SVM 매니저
	private SVMManager mSVMManger;

	// 파일 매니저
	private FileManager mFileManager;

	// 학습 진행 플래그
	private boolean mIsWalkingCompleted = false;
	private boolean mIsRunningCompleted = false;

	// 프로그레스 다이얼로그
	private ProgressDialog mMotionProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning_motion);

		// 생성
		mLearningWalkingBtn = (Button) findViewById(R.id.learning_walking_motion_btn);
		mLearningRunningBtn = (Button) findViewById(R.id.learning_running_motion_btn);
		mLearningCompleteBtn = (Button) findViewById(R.id.learning_complete_btn);
		mMotionLearner = new MotionLearner();
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mLearningData = new ArrayList<LearningData>();
		mSVMManger = new SVMManager();
		mFileManager = new FileManager();
		mMotionProgress = new ProgressDialog(this);

		// 클릭리스너 설정
		mLearningWalkingBtn.setOnClickListener(mClickListener);
		mLearningCompleteBtn.setOnClickListener(mClickListener);
		mLearningRunningBtn.setOnClickListener(mClickListener);

		// 프로그레스 세팅
		mMotionProgress.setTitle("학습 진행중");
		mMotionProgress.setMessage("학습이 진행중 입니다.");
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
					// 프로그레스 호출
					mMotionProgress.show();
					// 완료버튼 가리기
					mLearningCompleteBtn.setVisibility(View.INVISIBLE);

					// 특징 추출
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
					Toast.makeText(getApplicationContext(), "걷기 학습을 먼저 해주세요.",
							Toast.LENGTH_LONG).show();
					break;
				}
				// 프로그레스 호출
				mMotionProgress.show();
				if (mIsRunningCompleted == false) {
					// 완료버튼 가리기
					mLearningCompleteBtn.setVisibility(View.INVISIBLE);

					// 특징 추출
					handleData(mLearningData, FileManager.MOTION_DATA_FILE,
							SVMManager.LABEL_RUNNING);
				} else {
					Toast.makeText(getApplicationContext(),
							StringMessage.LEARNING_RUNNING_COMPLETED,
							Toast.LENGTH_LONG).show();
				}

				break;

			case R.id.learning_complete_btn:
				// 스케일 파일 생성 과정
				try {
					mSVMManger.scale(FileManager.MOTION_DATA_FILE, null,
							FileManager.RESTORE_MOTION_DATA_FILE);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							StringMessage.IOEXCEPTION_STRING, Toast.LENGTH_LONG)
							.show();
				}

				// 학습모델 생성
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

				// 학습 완료 토스트
				Toast.makeText(getApplicationContext(), "학습을 완료하였습니다.",
						Toast.LENGTH_LONG).show();

				break;
			}

		}
	};

	// 핸들링
	private void handleData(final ArrayList<LearningData> learningData,
			final String fileName, final String label) {
		// 3초 후 3초간 데이터 수집
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mSm.registerListener(mMotionLearner,
						mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						MotionOption.SENSOR_DELAY);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// 센서 해제
						mSm.unregisterListener(mMotionLearner);

						// 특징 추출
						separateData(learningData,
								mMotionLearner.mPreprocessedData);

						// 파일 저장
						try {
							mFileManager.makeInputFile(fileName, learningData,
									label);
						} catch (IOException e1) {
							e1.printStackTrace();
							Toast.makeText(getApplicationContext(),
									StringMessage.IOEXCEPTION_STRING,
									Toast.LENGTH_LONG).show();
						}

						// 데이터 리셋
						mMotionLearner.resetData();

						// 학습완료
						if (label == SVMManager.LABEL_WALKING) {
							Toast.makeText(getApplicationContext(),
									"걷기 학습을 완료하였습니다.", Toast.LENGTH_LONG)
									.show();
							// 완료 플래그
							mIsWalkingCompleted = true;
						} else if (label == SVMManager.LABEL_RUNNING) {
							Toast.makeText(getApplicationContext(),
									"뛰기 학습을 완료하였습니다.", Toast.LENGTH_LONG)
									.show();
							// 완료 플래그
							mIsRunningCompleted = true;
						}

						// 완료 버튼 생기게
						if (mIsRunningCompleted == true
								&& mIsWalkingCompleted == true)
							mLearningCompleteBtn.setVisibility(View.VISIBLE);

						// 프로그래스 해제
						mMotionProgress.dismiss();
					}
				}, MotionOption.LEARN_DATA_TIME);
			}
		}, MotionOption.LEARN_DELAY_TIME);
	}

	// 특징 추출
	private void extractCharacter(ArrayList<LearningData> learingData,
			ArrayList<Double> rawData) {

		// 특징추출
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

		// 학습 데이터에 추가
		learingData.add(temp);

	}

	// 수집한 센서데이터를 분리한다.
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
