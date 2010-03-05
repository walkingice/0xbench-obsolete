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
import java.nio.*;

public class CaseCanvas {
    public final String TAG = "CaseCanvas";

    public final static String PACKAGE = Benchmark.PACKAGE;
    public final static String TESTER  = PACKAGE + ".CanvasTester";
    private int mRepeatMax = 1;
    private int mRepeatNow;
    private long[] mResult;

    public final static String SOURCE_TAG = "SOURCE_TAG";
    public final static String INDEX  = "INDEX";
    public final static String RESULT = "RESULT";
    public final static String ROUND  = "ROUND";
    private int mCaseRound = 30;

    CaseCanvas() {
	this(1);
    }

    CaseCanvas(int repeat) {
	super();
	mRepeatMax = repeat;
	reset();
    }

    public String getTitle() {
	return TAG;
    }

    public Intent generateIntent() {
	/* if run out of the repeat times, go back directly */
	if (mRepeatNow <= 0) {
	    return null;
	}

	Intent intent = new Intent();
	intent.setClassName(PACKAGE, TESTER);
	intent.putExtra(ROUND, mCaseRound);
	intent.putExtra(SOURCE_TAG, TAG);

	intent.putExtra(INDEX, mRepeatNow);

	mRepeatNow = mRepeatNow -1;

	return intent;
    }

    public void clear() {
	mResult = new long[mRepeatMax];
	mRepeatNow = 0;
    }

    /* Reset the repeat time to default value. clear result */
    public void reset() {
	mResult = new long[mRepeatMax];
	mRepeatNow = mRepeatMax;
    }

    public boolean isFinish() {
	return (mRepeatNow == 0);
    }

    public void parseIntent(Intent intent) {
	if (intent == null) {
	    Log.i(TAG, "Intent is null");
	    return;
	}

	String tag = intent.getStringExtra(SOURCE_TAG);

	if (tag == null || !tag.equals(TAG)) {
	    Log.i(TAG,"Unknown intent, cannot parse it");
	    return;
	}

	int index = intent.getIntExtra(INDEX, -1);
	long result = intent.getLongExtra(RESULT, (long)-1);
	if (index == -1 || result == -1) {
	    Log.i(TAG,"Ooooops index=" + index + " result=" + result);
	    return;
	}

	mResult[index - 1] = result;
    }

    public String getResult() {
	if (!isFinish()) {
	    return TAG+" not finish yet";
	}

	String result = "";
	long total = 0;
	int length = mResult.length;
	for (int i = 0; i < length; i++) {
	    total = mResult[i];
	    result += "round " + i + ":" + mResult[i] + "\n";
	}

	result += "Average:" + (total/length) + "\n";
	return result;
    }
}
