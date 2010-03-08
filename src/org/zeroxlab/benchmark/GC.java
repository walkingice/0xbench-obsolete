package org.zeroxlab.ZeroXBench;

import org.zeroxlab.gc.GCBenchmark;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class GC extends Activity {

	private TextView mTextView1;

	public static Handler mHandler;
	public static final int GUINOTIFIER = 0x1234;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc);

		mTextView1 = (TextView) findViewById(R.id.myTextView1);

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case GC.GUINOTIFIER:
						mTextView1.setText(GCBenchmark.out);
						break;
				}
				super.handleMessage(msg);
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		new Thread() {
			public void run() {
				GCBenchmark.benchmark();
			}
		}.start();
	}
}
