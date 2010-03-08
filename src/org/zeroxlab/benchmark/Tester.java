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

    protected abstract String getTag();
    protected abstract int sleepBeforeStart();
    protected abstract int sleepBetweenRound();
    protected abstract void oneRound();

    protected String mSourceTag = "unknown";
    private boolean mNextRound = true;

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

    protected void startTester() {
	TesterThread thread = new TesterThread(sleepBeforeStart(), sleepBetweenRound());
	thread.start();
    }

    /* call this method if you finish your testing */
    public void finishTester(long start, long end) {
	long elapse = end - start;
	Intent intent = new Intent();
	Case.putResult(intent, elapse);
	if (mSourceTag == null || mSourceTag.equals("")) {
	    Case.putSource(intent, "unknown");
	} else {
	    Case.putSource(intent, mSourceTag);
	}

	Case.putIndex(intent, mIndex);
	setResult(0, intent);
	finish();
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
