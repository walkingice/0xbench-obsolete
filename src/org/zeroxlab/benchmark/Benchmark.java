package org.zeroxlab.benchmark;

import android.util.Log;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.nio.*;

import java.util.LinkedList;

/* Construct a basic UI */
public class Benchmark extends Activity implements View.OnClickListener {

    public final static String TAG     = "Benchmark";
    public final static String PACKAGE = "org.zeroxlab.benchmark";

    private Button   mRun;
    private CheckBox mCheckList[];
    private TextView mBannerInfo;

    private ScrollView   mScrollView;
    private LinearLayout mLinearLayout;

    LinkedList<Case> mCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mCases = new LinkedList<Case>();
	Case mycase = new CaseCanvas();
	Case glcube = new CaseGLCube();
	Case circle = new CaseDrawCircle();
	mCases.add(mycase);
	mCases.add(glcube);
	mCases.add(circle);
	initViews();
    }

    private void initViews() {
	mRun = (Button)findViewById(R.id.btn_run);
	mRun.setOnClickListener(this);

	mLinearLayout = (LinearLayout)findViewById(R.id.list_container);

	mBannerInfo = (TextView)findViewById(R.id.banner_info);

	int length = mCases.size();
	mCheckList = new CheckBox[length];
	for (int i = 0; i < length; i++) {
	    mCheckList[i] = new CheckBox(this);
	    mCheckList[i].setText(mCases.get(i).getTitle());
	    mLinearLayout.addView(mCheckList[i]);
	}
    }

    public void onClick(View v) {
	if (v == mRun) {
	    for (int i = 0; i < mCheckList.length; i++) {
		if (mCheckList[i].isChecked()) {
		    mCases.get(i).reset();
		} else {
		    mCases.get(i).clear();
		}
	    }
	    runCase(mCases);
	}
    }

    public void runCase(LinkedList<Case> list) {
	Case pointer = null;
	boolean finish = true;
	for (int i = 0; i < list.size(); i++) {
	    pointer = list.get(i);
	    if (!pointer.isFinish()) {
		finish = false;
		break;
	    }
	}

	if (finish) {
	    mBannerInfo.setText(getResult());
	} else {
	    Intent intent = pointer.generateIntent();
	    if (intent != null) {
		startActivityForResult(intent, 0);
	    }
	}
    }

    public String getResult() {
	String result = "";
	Case mycase;
	for (int i = 0; i < mCases.size(); i++) {
	    mycase = mCases.get(i);
	    result += mycase.getTitle() + "\n";
	    result += mycase.getBenchmark()+"\n";
	}

	return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (data == null) {
	    Log.i(TAG,"oooops....Intent is null");
	    return;
	}

	Case mycase;
	for (int i = 0; i < mCases.size(); i++) {
	    mycase = mCases.get(i);
	    if (mycase.realize(data)) {
		mycase.parseIntent(data);
		break;
	    }
	}
	runCase(mCases);
    }
}
