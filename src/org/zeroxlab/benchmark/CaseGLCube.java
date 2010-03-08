package org.zeroxlab.benchmark;

import android.util.Log;

import org.zeroxlab.kubench.Kubench;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import java.nio.*;

public class CaseGLCube extends Case{

    CaseGLCube() {
	super("CaseGLCube", Kubench.getFullClassName(), 3, 80);
    }

    public String getDescription() {
	return "OpenGL Cube benchmark";
    }

    @Override
    public String getBenchmark() {
	if (!isFinish()) {
	    return TAG + " not finish yet";
	}

	if (mInvolved == false) {
	    Log.i(TAG, " is not a benchmark target");
	    return TAG + " is not a benchmark target";
	} else {
	    Log.i(TAG, " is a benchmark target");
	}

	String result = "";
	long total = 0;
	int length = mResult.length;

	for (int i = 0; i < length; i++) {
	    long fps = mCaseRound / (mResult[i] / 1000); // milliseconds to seconds
	    result += "Round " + i + ": fps = " + fps + "\n";
	    total  += fps;
	}

	result += "Average: fps = " + (total/length) + "\n";
	return result;
    }
}
