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
import android.graphics.Canvas;
import android.os.*;

import java.util.Random;

public class TesterCanvas extends Tester {
    public final String TAG = "TesterCanvas";
    public final static String PACKAGE = "org.zeroxlab.benchmark";
    MyView mView;

    public String getTag() {
	return TAG;
    }

    public static String getPackage() {
	return PACKAGE;
    }

    public static String getFullClassName() {
	return getPackage()+".TesterCanvas";
    }

    public int sleepBetweenRound() {
	return 15;
    }
    public int sleepBeforeStart() {
	return 1000;
    }

    public void oneRound() {
	mView.postInvalidate();
    }

    public void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	mView = new MyView(this);
	setContentView(mView);
    }

    class MyView extends View {
	int i = 0;
	Random mRandom;

	MyView(Context context) {
	    super(context);
	    mRandom = new Random();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
	    super.onWindowVisibilityChanged(visibility);
	    if (visibility != View.VISIBLE) {
		return;
	    }

	    startTester();
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    int r = 0xFF & mRandom.nextInt();
	    canvas.drawRGB(r, r, r);
	    decreaseCounter();
	}
    }
}
