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
    CaseGC() {
	super("CaseGC", "org.zeroxlab.benchmark.GC", 1, 1); // GC benchmark only run once
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

	int index  = Case.getIndex(intent);
	String result = intent.getStringExtra(GCRESULT) ;

	if (result == null || result.equals("")) {
	    mStringBuf += "\nReport not found\n";
	} else {
	    mStringBuf += "\n"+result+"\n";
	}
    }
}
