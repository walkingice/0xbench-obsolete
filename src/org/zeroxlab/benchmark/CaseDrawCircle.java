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

public class CaseDrawCircle extends Case{

    CaseDrawCircle() {
	super("CaseDrawCircle", "org.zeroxlab.graphics.DrawCircle", 1, 300);
    }

    public String getDescription() {
	return "Hi";
    }

    @Override
    public String getBenchmark() {
	if (!couldFetchReport()) {
	    return "DrawCircle has no report";
	}

	String result = "";
	long total = 0;
	int length = mResult.length;

	for (int i = 0; i < length; i++) {
	    float second = (mResult[i] / 1000f);
	    float fps = (float)mCaseRound / second; // milliseconds to seconds
	    result += "Round " + i +": fps = " + fps + "\n";
	    total  += fps;
	}

	result += "Average: fps = " + (total/length) + "\n";
	return result;
    }
}
