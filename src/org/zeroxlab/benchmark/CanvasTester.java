package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.graphics.Canvas;
import android.os.*;

public class CanvasTester extends Activity{
    public final String TAG = "CanvasTester";
    MyView mView;
    int mRound;
    int mNow;
    int mIndex;

    private String mSourceTag = "unknown";

    public void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	Intent intent = getIntent();
	mView = new MyView(this);
	if (intent != null) {
	    mRound = intent.getIntExtra(CaseCanvas.ROUND, 100);
	    mSourceTag = intent.getStringExtra(CaseCanvas.SOURCE_TAG);
	    mIndex = intent.getIntExtra(CaseCanvas.INDEX, -1);
	} else {
	    mRound = 80;
	    mIndex = -1;
	}
	mNow   = mRound;
	Log.i(TAG,"index = " + mIndex);
	setContentView(mView);
    }

    public void onDestroy() {
	super.onDestroy();
    }

    protected void TestFinish(long start, long end) {
	long elapse = end - start;
	Intent intent = new Intent();
	intent.putExtra(CaseCanvas.RESULT, elapse);
	if (mSourceTag == null || mSourceTag.equals("")) {
	    intent.putExtra(CaseCanvas.SOURCE_TAG, "unknown");
	} else {
	    intent.putExtra(CaseCanvas.SOURCE_TAG, mSourceTag);
	}

	intent.putExtra(CaseCanvas.INDEX, mIndex);
	setResult(0, intent);
	finish();
    }

    class MyView extends View {
	int i = 0;

	MyView(Context context) {
	    super(context);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
	    super.onWindowVisibilityChanged(visibility);
	    if (visibility != View.VISIBLE) {
		return;
	    }

	    MyThread t = new MyThread(this);
	    t.start();
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    i = i + 3;
	    i = i % 0xff;
	    canvas.drawRGB(i, i, i);
	    mNow--;
	    Log.i(TAG,"mNow = "+mNow);
	}
    }

    class MyThread extends Thread {
	View mView;
	MyThread(View target) {
	    mView = target;
	}

	public void run() {
	    try {
		sleep(1000);
	    } catch (Exception e) {}

	    long start = SystemClock.uptimeMillis();
	    while (mNow > 0) {
		mView.postInvalidate();
	    }
	    long end = SystemClock.uptimeMillis();
	    TestFinish(start, end);
	}
    }
}
