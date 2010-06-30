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
import java.util.ArrayList;

public class CaseGC extends Case{

    String mStringBuf = "";
    public static String GCRESULT = "GC_RESULT";
    public static String TIME = "GC_RUNTIME";
    public static double time = 0.0;

    CaseGC() {
	super("CaseGC", "org.zeroxlab.benchmark.TesterGC", 1, 1); // GC benchmark only run once

    mUnit = "msec";
    String [] _tmp = {
        "dalvik",
        "garbagecollection",
    };
    mTags = _tmp;
    }

    public String getTitle() {
	return "Garbage Collection";
    }

    public String getDescription() {
	return "It create long-live binary tree of depth and array of doubles to test GC";
    }

    @Override
    public void clear() {
	super.clear();
	mStringBuf = "";
    }

    @Override
    public void reset() {
	super.reset();
	mStringBuf = "";
    }

    @Override
    public String getBenchmark() {

	if (!couldFetchReport()) {
	    return "No benchmark report";
	}

	return mStringBuf;
    }

    @Override
    public ArrayList<Scenario> getScenarios () {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

    Scenario s = new Scenario(getTitle(), mTags, mUnit);
    s.mLog = getBenchmark();
    s.mResults.add(time);
    scenarios.add(s);

    return scenarios;
    }

    @Override
    protected boolean saveResult(Intent intent, int index) {
	String result = intent.getStringExtra(GCRESULT);
    time = intent.getDoubleExtra(TIME, 0.0);

	if (result == null || result.equals("")) {
	    mStringBuf += "\nReport not found\n";
	} else {
	    mStringBuf += "\n"+result+"\n";
	}

	return true;
    }
}
