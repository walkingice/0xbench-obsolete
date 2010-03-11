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

public abstract class Case{
    protected String TAG = "Case";

    protected String PACKAGE = Benchmark.PACKAGE;
    protected String TESTER;
    private int mRepeatMax = 1;
    private int mRepeatNow;
    protected boolean mInvolved;
    protected long[] mResult;

    private final static String SOURCE = "SOURCE";
    private final static String INDEX  = "INDEX";
    private final static String RESULT = "RESULT";
    private final static String ROUND  = "ROUND";
    protected int mCaseRound = 30;

    /**
     * Constructor to generate instance.
     *
     * It defines the Case as "Please run Tester for N round, repeat N times"
     * @param tag The tag name of the subclass Case. It is generally the Subclass Name
     * @param tester The taget tester be used by subclass Case. It should be full class name.
     * @param repeat The tester will run *repeat* times.
     * @param round To tell tester to run itself as *round* round.
     */
    protected Case(String tag, String tester, int repeat, int round) {
	TAG    = tag;
	TESTER = tester;
	mRepeatMax = repeat;
	mCaseRound = round;
	reset();
    }

    abstract public String getDescription();
    abstract public String getTitle();

    public final static void putRound(Intent intent, int round) {
	intent.putExtra(ROUND, round);
    }

    public final static void putIndex(Intent intent, int index) {
	intent.putExtra(INDEX, index);
    }

    public final static void putSource(Intent intent, String source) {
	intent.putExtra(SOURCE, source);
    }

    public final static void putResult(Intent intent, long result) {
	intent.putExtra(RESULT, result);
    }

    public final static int getRound(Intent intent) {
	return intent.getIntExtra(ROUND, 100);
    }

    public final static int getIndex(Intent intent) {
	return intent.getIntExtra(INDEX, -1);
    }

    public final static String getSource(Intent intent) {
	String source = intent.getStringExtra(SOURCE);
	if (source == null) {
	    return "unknown";
	}

	if (source.equals("")) {
	    return "unknown";
	}

	return source;
    }

    public final static long getResult(Intent intent) {
	long defaultResult = -1;
	return intent.getLongExtra(RESULT, defaultResult);
    }

    public String getTag() {
	return TAG;
    }

    protected Intent generateIntent() {
	/* if run out of the repeat times, go back directly */
	if (mRepeatNow <= 0) {
	    return null;
	}

	Intent intent = new Intent();
	intent.setClassName(PACKAGE, TESTER);
	Case.putRound(intent, mCaseRound);
	Case.putSource(intent, TAG);
	Case.putIndex(intent, mRepeatNow);

	mRepeatNow = mRepeatNow - 1;

	return intent;
    }

    public void clear() {
	mResult = new long[mRepeatMax];
	mRepeatNow = 0;
	mInvolved  = false;
    }

    /* Reset the repeat time to default value. clear result */
    public void reset() {
	mResult = new long[mRepeatMax];
	mRepeatNow = mRepeatMax;
	mInvolved  = true;
    }

    public boolean isFinish() {
	return (mRepeatNow == 0);
    }

    /** To read the SOURCE of this intent to see if this intent belong to this case
     *
     * @return return True if this intent belong to this case, otherwise return false
     */
    public boolean realize(Intent intent) {
	if (intent == null) {
	    Log.i(TAG, "Intent is null");
	    return false;
	}

	String source = Case.getSource(intent);
	if (source == null || source.equals("")) {
	    return false;
	}

	if (source.equals(TAG)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean parseIntent(Intent intent) {
	if (intent == null) {
	    Log.i(TAG, "Intent is null");
	    return false;
	}

	String tag = Case.getSource(intent);

	if (tag == null || !tag.equals(TAG)) {
	    Log.i(TAG,"Unknown intent, cannot parse it");
	    return false;
	}

	int  index  = Case.getIndex(intent);
	if (index <= 0) {
	    Log.i(TAG,"Ooooops index <= 0, how come?");
	    return false;
	}

	return saveResult(intent, index);
    }

    /**
     * To Save the result from Tester into this Case
     * If subclass has its own way to analysis result, override this method
     *
     * @param intent The intent will be analysis
     * @param index The repeating time of this intent. (Tester might repeat N times)
     * @return return True if analysis sucessfully
     */
    protected boolean saveResult(Intent intent, int index) {
	long result = Case.getResult(intent);

	if (result == -1) {
	    Log.i(TAG,"Oooops! result is " + result);
	    return false;
	}

	mResult[index -1] = result;
	return true;
    }

    public boolean couldFetchReport() {
	if (!isFinish()) {
	    return false;
	}

	if (mInvolved == false) {
	    return false;
	}

	return true;
    }
    public String getBenchmark() {

	if (!couldFetchReport()) {
	    return "No benchmark report";
	}

	String result = "";
	long total = 0;
	int length = mResult.length;
	for (int i = 0; i < length; i++) {
	    total  += mResult[i];
	    result += "round " + i + ":" + mResult[i] + "\n";
	}

	result += "Average:" + (total/length) + "\n";
	return result;
    }
}
