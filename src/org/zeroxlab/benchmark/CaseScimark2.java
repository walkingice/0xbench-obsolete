package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

import org.zeroxlab.benchmark.TesterScimark2;

public class CaseScimark2 extends Case{

    public static String LIN_RESULT = "LIN_RESULT";
    protected Bundle mInfo[];

    public static int Repeat = 1;
    public static int Round  = 1;

    CaseScimark2() {
	super("CaseScimark2", "org.zeroxlab.benchmark.TesterScimark2", Repeat, Round);
	generateInfo();
    }

    public String getTitle() {
	return "Scimark2";
    }

    public String getDescription() {
	return "SciMark 2.0 is a Java benchmark for scientific and numerical computing. It measures several computational kernels and reports a composite score in approximate Mflops.";
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
	    result += TesterScimark2.bundleToString(mInfo[i]);
	    result += "\n";
	}
	return result;
    }

    @Override
    protected boolean saveResult(Intent intent, int index) {
	Bundle info = intent.getBundleExtra(LIN_RESULT);
	if (info == null) {
	    Log.i(TAG, "Weird! cannot find Scimark2Info");
	    return false;
	} else {
	    mInfo[index] = info;
	}

	return true;
    }
}
