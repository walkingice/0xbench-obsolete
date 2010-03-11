package org.zeroxlab.benchmark;

import org.zeroxlab.gc.GCBenchmark;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.TextView;

public class TesterGC extends Tester{

	private TextView mTextView1;

	public static Handler mHandler;
	public static final int GUINOTIFIER = 0x1234;


	protected String getTag() {
	    return "GC";
	}

	protected int sleepBeforeStart() {
	    return 1000;
	}

	protected int sleepBetweenRound() {
	    return 200;
	}

	protected void oneRound() {
	    GCBenchmark.benchmark();
	    decreaseCounter();
	}

	@Override
	public void finishTester(long start, long end) {
	    Intent intent = new Intent();
	    if (mSourceTag == null || mSourceTag.equals("")) {
		Case.putSource(intent, "unknown");
	    } else {
		Case.putSource(intent, mSourceTag);
	    }

	    Case.putIndex(intent, mIndex);

	    intent.putExtra(CaseGC.GCRESULT, GCBenchmark.out.toString());
	    setResult(0, intent);
	    finish();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc);

		mTextView1 = (TextView) findViewById(R.id.myTextView1);

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case GUINOTIFIER:
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
		startTester();
	}
}
