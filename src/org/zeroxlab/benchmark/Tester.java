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

public abstract class Tester extends Activity{
    private String TAG;
    public final static String PACKAGE = "org.zeroxlab.benchmark";
    int mRound;
    int mNow;
    int mIndex;

    protected long mTesterStart = 0;
    protected long mTesterEnd   = 0;

    protected abstract String getTag();
    protected abstract int sleepBeforeStart();
    protected abstract int sleepBetweenRound();
    protected abstract void oneRound();

    protected String mSourceTag = "unknown";
    private boolean mNextRound = true;

    protected boolean mDropTouchEvent     = true;
    protected boolean mDropTrackballEvent = true;

    public void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	TAG = getTag();

	Intent intent = getIntent();
	if (intent != null) {
	    mRound     = Case.getRound(intent);
	    mSourceTag = Case.getSource(intent);
	    mIndex     = Case.getIndex(intent);
	} else {
	    mRound = 80;
	    mIndex = -1;
	}
	mNow   = mRound;
    }

    @Override
    protected void onPause() {
	super.onPause();
	interruptTester();
    }

    /* drop the annoying event */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
	if (mDropTouchEvent) {
	    return false;
	} else {
	    return super.dispatchTouchEvent(ev);
	}
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent ev) {
	if (mDropTrackballEvent) {
	    return false;
	} else {
	    return super.dispatchTouchEvent(ev);
	}
    }

    protected void startTester() {
	TesterThread thread = new TesterThread(sleepBeforeStart(), sleepBetweenRound());
	thread.start();
    }

    public void interruptTester() {
	mNow = 0;
	finish();
    }

    /**
     * Call this method if you finish your testing.
     *
     * @param start The starting time of testing round
     * @param end The ending time of testing round
     */
    public void finishTester(long start, long end) {
	mTesterStart = start;
	mTesterEnd   = end;
	Intent intent = new Intent();
	if (mSourceTag == null || mSourceTag.equals("")) {
	    Case.putSource(intent, "unknown");
	} else {
	    Case.putSource(intent, mSourceTag);
	}

	Case.putIndex(intent, mIndex);
	saveResult(intent);

	setResult(0, intent);
	finish();
    }

    /**
     * Save the benchmarking result into intent
     * If this Case and Tester has their own way to pass benchmarking result
     * just override this method
     *
     * @param intent The intent will return to Case
     */
    protected boolean saveResult(Intent intent) {
	long elapse = mTesterEnd - mTesterStart;
	Case.putResult(intent, elapse);
	return true;
    }

    public void resetCounter() {
	mNow = mRound;
    }

    public void decreaseCounter() {
	mNow = mNow - 1;
	mNextRound = true;
    }

    public boolean isTesterFinished() {
	return (mNow <= 0);
    }

    class TesterThread extends Thread {
	int mSleepingStart;
	int mSleepingTime;
	TesterThread(int sleepStart, int sleepPeriod) {
	    mSleepingStart = sleepStart;
	    mSleepingTime  = sleepPeriod;
	}

	private void lazyLoop() throws Exception {
	    while (!isTesterFinished()) {
		if (mNextRound) {
		    mNextRound = false;
		    oneRound();
		} else {
		    sleep(mSleepingTime);
		}
	    }
	}

	private void nervousLoop() throws Exception {
	    while (!isTesterFinished()) {
		oneRound();
	    }
	}

	public void run() {
	    try {
		sleep(mSleepingStart);

		long start = SystemClock.uptimeMillis();

		if (mSleepingTime == 0) {
		    nervousLoop();
		} else {
		    lazyLoop();
		}

		long end = SystemClock.uptimeMillis();
		finishTester(start, end);
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	}
    }

}
