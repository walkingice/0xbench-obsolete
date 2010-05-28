package org.zeroxlab.benchmark;

import jnt.scimark2.commandline;

import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.TextView;

public class TesterScimark2 extends Tester{

    TextView mTextView;
    Bundle mInfo[];
    public final static String COMPOSITE    = "COMPOSITE";
    public final static String FTT          = "FTT";
    public final static String SOR          = "SOR";
    public final static String MONTECARLO   = "MONTECARLO";
    public final static String SPARSEMATMULT= "SPARSEMATMULT";
    public final static String LU           = "LU";

    protected String getTag() {
	return "Scimark2";
    }

    protected int sleepBeforeStart() {
	return 1000;
    }

    protected int sleepBetweenRound() {
	return 200;
    }

    protected void oneRound() {
	commandline.main(mInfo[mNow - 1]);
	decreaseCounter();
    }

    @Override
    protected boolean saveResult(Intent intent) {
        Bundle result = new Bundle();
        TesterScimark2.average(result, mInfo);
    
        intent.putExtra(CaseScimark2.LIN_RESULT, result);
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
	    Log.i("Scimark2", "Array is null");
	    return;
	}

	int length = list.length;
	double composite_total     = 0.0;
	double ftt_total           = 0.0;
	double sor_total           = 0.0;
	double montecarlo_total    = 0.0;
	double sparsematmult_total = 0.0;
	double lu_total            = 0.0;

	for (int i = 0; i < length; i ++) {
	    Bundle info = list[i];

	    if (info == null) {
		Log.i("Scimark2", "one item of array is null!");
		return;
	    }

	    composite_total     += info.getDouble(COMPOSITE    );
	    ftt_total           += info.getDouble(FTT          );
	    sor_total           += info.getDouble(SOR          );
	    montecarlo_total    += info.getDouble(MONTECARLO   );
	    sparsematmult_total += info.getDouble(SPARSEMATMULT);
	    lu_total            += info.getDouble(LU           );
	}

	result.putDouble(COMPOSITE    , composite_total    / length);
	result.putDouble(FTT          , ftt_total          / length);
	result.putDouble(SOR          , sor_total          / length);
	result.putDouble(MONTECARLO   , montecarlo_total   / length);
	result.putDouble(SPARSEMATMULT, sparsematmult_total/ length);
	result.putDouble(LU           , lu_total           / length);
    }

    public static String bundleToString(Bundle bundle) {
	String result = "";
	result += "\nComposite:\n  " + bundle.getDouble(COMPOSITE, 0.0);
	result += "\nFast Fourier Transform:\n  " + bundle.getDouble(FTT, 0.0);
	result += "\nJacobi Successive Over-relaxation:\n  " + bundle.getDouble(SOR, 0.0);
	result += "\nMonte Carlo integration:\n  " + bundle.getDouble(MONTECARLO, 0.0);
	result += "\nSparse matrix multiply:\n  " + bundle.getDouble(SPARSEMATMULT, 0.0);
	result += "\ndense LU matrix factorization:\n  " + bundle.getDouble(LU, 0.0);

	return result;
    }

}
