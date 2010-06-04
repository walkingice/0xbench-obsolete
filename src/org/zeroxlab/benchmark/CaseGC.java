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

public class CaseGC extends Case{

    String mStringBuf = "";
    public static String GCRESULT = "GC_RESULT";
    public static String TIME = "GC_RUNTIME";
    public static double time = 0.0;
    CaseGC() {
	super("CaseGC", "org.zeroxlab.benchmark.TesterGC", 1, 1); // GC benchmark only run once
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
	return mStringBuf;
    }

    @Override
    public String getXMLBenchmark() {
        if (time == 0.0) {
            return "";
        }
        String result = "";
        result += "<scenario benchmark=\"GarbageCollection\" unit=\"runtime_ns\">";
        result += "" + time/1000f + "</scenario>";
        return result;
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
