package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

import org.zeroxlab.benchmark.TesterArithmetic;

public class CaseArithmetic extends Case{

    public static String LIN_RESULT = "LIN_RESULT";
    protected Bundle mInfo[];

    public static int Repeat = 1;
    public static int Round  = 3;

    CaseArithmetic() {
	super("CaseArithmetic", "org.zeroxlab.benchmark.TesterArithmetic", Repeat, Round);
	generateInfo();
    }

    public String getTitle() {
	return "Arithmetic";
    }

    public String getDescription() {
	return "The Linpack Benchmark is a numerically intensive test that has been used for years to measure the floating point performance of computers.";
    }

    private void generateInfo() {
	mInfo = new Bundle[Repeat];
	for (int i = 0; i < mInfo.length; i++) {
	    mInfo[i] = new Bundle();
	}
    }

    @Override
    public void clear() {
	super.clear();
	generateInfo();
    }

    @Override
    public void reset() {
	super.reset();
	generateInfo();
    }

    @Override
    public String getBenchmark() {
	String result = "\n";
	for (int i = 0; i < mInfo.length; i++) {
	    result += TesterArithmetic.bundleToString(mInfo[i]);
	    result += "\n";
	}
	return result;
    }

    @Override
    public void parseIntent(Intent intent) {
    	if (intent == null) {
	    Log.i(TAG, "Intent is null");
	    return;
	}

	String tag = Case.getSource(intent);

	if (tag == null || !tag.equals(TAG)) {
	    Log.i(TAG,"Unknown intent, cannot parse it");
	    return;
	}

	int index  = Case.getIndex(intent) - 1;
	Bundle info = intent.getBundleExtra(LIN_RESULT) ;
	if (info == null) {
	    Log.i(TAG, "Weird! cannot find LinpackInfo");
	} else {
	    mInfo[index] = info;
	}
    }
}
