package org.zeroxlab.benchmark;

import android.util.Log;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.nio.*;

/* Construct a basic UI */
public class Benchmark extends Activity {

    private Button   mRun;
    private CheckBox mCheckList[];

    private ScrollView   mScrollView;
    private LinearLayout mLinearLayout;

    private String test[] = {"aaa", "bbb", "ccc",
	"ddd", "eee", "fff", "ggg", "hhh", "iii",
	"jjj", "kkk"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	initViews();
    }

    private void initViews() {
	mRun = (Button)findViewById(R.id.btn_run);
	mLinearLayout = (LinearLayout)findViewById(R.id.list_container);

	int length = test.length;
	mCheckList = new CheckBox[length];
	for (int i = 0; i < length; i++) {
	    mCheckList[i] = new CheckBox(this);
	    mCheckList[i].setText(test[i]);
	    mLinearLayout.addView(mCheckList[i]);
	}
    }
}
