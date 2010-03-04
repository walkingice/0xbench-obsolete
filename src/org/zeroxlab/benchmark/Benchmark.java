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
public class Benchmark extends Activity implements View.OnClickListener {

    private Button   mRun;
    private CheckBox mCheckList[];
    private TextView mBannerInfo;

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
	mRun.setOnClickListener(this);

	mLinearLayout = (LinearLayout)findViewById(R.id.list_container);

	mBannerInfo = (TextView)findViewById(R.id.banner_info);

	int length = test.length;
	mCheckList = new CheckBox[length];
	for (int i = 0; i < length; i++) {
	    mCheckList[i] = new CheckBox(this);
	    mCheckList[i].setText(test[i]);
	    mLinearLayout.addView(mCheckList[i]);
	}
    }

    public void onClick(View v) {
	if (v == mRun) {
	    String result = "result\n";
	    int length = mCheckList.length;
	    for (int i = 0; i < length; i++) {
		result += test[i] + ":" + mCheckList[i].isChecked() + "\n";
		mCheckList[i].setChecked(false);
	    }

	    mBannerInfo.setText(result);
	}
    }
}
