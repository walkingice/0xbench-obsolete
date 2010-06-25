package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class CaseNativePs extends Case{

    public static String LIN_RESULT = "LIN_RESULT";
    protected Bundle mInfo[];

    public static int Repeat = 1;
    public static int Round  = 1;

    CaseNativePs() {
	super("CaseNativePs", "org.zeroxlab.benchmark.NativeTesterPs", Repeat, Round);

    mType = "Test";
    mUnit = "text";
    String [] _tmp = {
        "test",
    };
    mTags = _tmp;

	generateInfo();
    }

    public String getTitle() {
	return "NativePs";
    }

    public String getDescription() {
	return "test.";
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
	if (!couldFetchReport()) {
	    return "No benchmark report";
	}

	String result = "\n";
	return result;
    }

    @Override
    public ArrayList<Scenario> getScenarios () {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

    return scenarios;
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
