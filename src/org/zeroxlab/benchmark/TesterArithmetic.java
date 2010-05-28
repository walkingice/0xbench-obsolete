package org.zeroxlab.benchmark;

import org.zeroxlab.arithmetic.LinpackLoop;

import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.TextView;

public class TesterArithmetic extends Tester{

    TextView mTextView;
    Bundle mInfo[];
    public final static String MFLOPS = "MFLOPS";
    public final static String RESIDN = "RESIDN";
    public final static String TIME   = "TIME";
    public final static String EPS    = "EPS";

    protected String getTag() {
	return "Arithmetic";
    }

    protected int sleepBeforeStart() {
	return 1000;
    }

    protected int sleepBetweenRound() {
	return 200;
    }

    protected void oneRound() {
	LinpackLoop.main(mInfo[mNow - 1]);
	decreaseCounter();
    }

    @Override
    protected boolean saveResult(Intent intent) {
        Bundle result = new Bundle();
        TesterArithmetic.average(result, mInfo);
    
        intent.putExtra(CaseArithmetic.LIN_RESULT, result);
	return true;
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	int length = mRound;
	mInfo = new Bundle[length];
	for (int i = 0; i < length; i++) {
	    mInfo[i] = new Bundle();
	}

	mTextView = new TextView(this);
	mTextView.setText("Running benchmark....");
	mTextView.setTextSize(mTextView.getTextSize() + 5);
	setContentView(mTextView);
	startTester();
    }

    public static void average(Bundle result, Bundle[] list) {

	if (result == null) {
	    result = new Bundle();
	}

	if (list == null) {
	    Log.i("Arithmetic", "Array is null");
	    return;
	}

	int length = list.length;
	double mflops_total  = 0.0;
	double residn_total  = 0.0;
	double time_total    = 0.0;
	double eps_total     = 0.0;

	for (int i = 0; i < length; i ++) {
	    Bundle info = list[i];

	    if (info == null) {
		Log.i("Arithmetic", "one item of array is null!");
		return;
	    }

	    mflops_total  += info.getDouble(MFLOPS);
	    residn_total  += info.getDouble(RESIDN);
	    time_total    += info.getDouble(TIME);
	    eps_total     += info.getDouble(EPS);
	}

	result.putDouble(MFLOPS, mflops_total / length);
	result.putDouble(RESIDN, residn_total / length);
	result.putDouble(TIME, time_total / length);
	result.putDouble(EPS, eps_total  / length);
    }

    public static String bundleToString(Bundle bundle) {
	String result = "";
	result += "\nMflops/s :" + bundle.getDouble(MFLOPS, 0.0);
	/* the time result is too small to calculate average. (0.0 ~ 0.1), drop it*/
	//result += "\nTime     :" + bundle.getDouble(TIME, 0.0);
	result += "\nNorm Res :" + bundle.getDouble(RESIDN, 0.0);
	result += "\nPrecision:" + bundle.getDouble(EPS, 0.0);

	return result;
    }

    public static String bundleListToXML(Bundle[] mInfo) {

	String result = "";
	double total = 0.0;
	double max = mInfo[0].getDouble(MFLOPS, 0.0);
	double min = mInfo[0].getDouble(MFLOPS, 0.0);
	int length = mInfo.length;
	for (int i = 0; i < length; i++) {
		double mflops = mInfo[i].getDouble(MFLOPS, 0.0);
	    total += mflops;
		if (mflops > max) {
			max = mflops;
		} else if (mflops < min) {
			min = mflops;
		}
	}

	result += "<scenario benchmark=\"Linpack\">";
	result += min + " " + (total/length) + " " + max;
	result += "</scenario>";

	return result;

	}

}
