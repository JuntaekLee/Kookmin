package kr.ac.kookmin.cs.activity;

import kr.ac.kookmin.cs.svm.FileManager;
import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button mStartBtn;
	private Button mStopBtn;
	private Button mLearningActivityBtn;
	private BackgroundCollector mBackgroundService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mStartBtn = (Button) findViewById(R.id.background_start_btn);
		mStopBtn = (Button) findViewById(R.id.background_stop_btn);
		mLearningActivityBtn = (Button) findViewById(R.id.learning_activity_start_btn);
		mBackgroundService = new BackgroundCollector();
		//mBackgroundService = new BackgroundCollector();

		mStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startService(new Intent(getApplicationContext(),
						BackgroundCollector.class));
			}
		});

		mStopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopService(new Intent(getApplicationContext(),
						BackgroundCollector.class));
			}
		});
		
		mLearningActivityBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LearningMotionActivity.class);
				startActivity(intent);
				
			}
		});
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
