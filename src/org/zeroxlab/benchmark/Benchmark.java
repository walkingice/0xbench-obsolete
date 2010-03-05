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

    LinkedList<CaseCanvas> mCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mCases = new LinkedList<CaseCanvas>();
	CaseCanvas mycase = new CaseCanvas(3);
	mCases.add(mycase);
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

    public void runCase(LinkedList<CaseCanvas> list) {
	CaseCanvas pointer = null;
	boolean finish = true;
	for (int i = 0; i < list.size(); i++) {
	    pointer = list.get(i);
	    if (!pointer.isFinish()) {
		finish = false;
		break;
	    }
	}

	if (finish) {
	    mBannerInfo.setText("Finished");
	} else {
	    Intent intent = pointer.generateIntent();
	    if (intent != null) {
		startActivityForResult(intent, 0);
	    }
	}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (data == null) {
	    Log.i(TAG,"oooops....Intent is null");
	    return;
	}
	runCase(mCases);
    }
}
