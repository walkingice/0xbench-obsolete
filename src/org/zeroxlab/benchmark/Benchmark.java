package org.zeroxlab.benchmark;

import android.util.Log;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.nio.*;
import java.io.*;

import java.util.LinkedList;
import java.util.Date;
import java.text.SimpleDateFormat;

/* Construct a basic UI */
public class Benchmark extends Activity implements View.OnClickListener {

    public final static String TAG     = "Benchmark";
    public final static String PACKAGE = "org.zeroxlab.benchmark";

    private final static String SDCARD      = "/sdcard";
    private final static String mOutputFile = "0xBenchmark";

	private final static String postUrl = "http://bzsandbox.appspot.com:80/run/";
	private final static String apiKey = "0a57da89-5242-4059-92c2-fa7a7d3b3dd0";
	private final static String benchmarkName = "0xDebug";

    private Button   mRun;
    private Button   mShow;
    private CheckBox mCheckList[];
    private TextView mDesc[];
    private TextView mBannerInfo;

    private ScrollView   mScrollView;
    private LinearLayout mLinearLayout;

    LinkedList<Case> mCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mCases = new LinkedList<Case>();
	Case arith  = new CaseArithmetic();
	Case scimark2  = new CaseScimark2();
	Case mycase = new CaseCanvas();
	Case glcube = new CaseGLCube();
	Case circle = new CaseDrawCircle();
	Case gc     = new CaseGC();
	Case nehe08 = new CaseNeheLesson08();
	Case nehe16 = new CaseNeheLesson16();
	Case teapot = new CaseTeapot();
	mCases.add(arith);
	mCases.add(scimark2);
	mCases.add(mycase);
	mCases.add(glcube);
	mCases.add(nehe08);
	mCases.add(nehe16);
	mCases.add(teapot);
	mCases.add(circle);
	mCases.add(gc);
	initViews();
    }

    private void initViews() {
	mRun = (Button)findViewById(R.id.btn_run);
	mRun.setOnClickListener(this);

	mShow = (Button)findViewById(R.id.btn_show);
	mShow.setOnClickListener(this);

	mLinearLayout = (LinearLayout)findViewById(R.id.list_container);

	mBannerInfo = (TextView)findViewById(R.id.banner_info);
	mBannerInfo.setText("Select benchmarking target and click Run");

	int length = mCases.size();
	mCheckList = new CheckBox[length];
	mDesc      = new TextView[length];
	boolean gray = true;
	for (int i = 0; i < length; i++) {
	    mCheckList[i] = new CheckBox(this);
	    mCheckList[i].setText(mCases.get(i).getTitle());
	    mLinearLayout.addView(mCheckList[i]);
	    mDesc[i] = new TextView(this);
	    mDesc[i].setText(mCases.get(i).getDescription());
	    mDesc[i].setTextSize(mDesc[i].getTextSize() - 2);
	    mDesc[i].setPadding(42, 0, 10, 10);
	    mLinearLayout.addView(mDesc[i]);

	    if (gray) {
		int color = 0xFF333333; //ARGB
		mCheckList[i].setBackgroundColor(color);
		mDesc[i].setBackgroundColor(color);
	    }

	    gray = !gray;
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
	} else if (v == mShow) {
		uploadResult();
	    String result = getResult();
	    Log.i(TAG,"\n\n"+result+"\n\n");
	    writeToSDCard(mOutputFile, result);
	    Intent intent = new Intent();
	    intent.putExtra(Report.REPORT, result);
	    intent.setClassName(Report.packageName(), Report.fullClassName());
	    startActivity(intent);
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
	    mBannerInfo.setText("Benchmark finished. click show to read report");
	} else {
	    Intent intent = pointer.generateIntent();
	    if (intent != null) {
		startActivityForResult(intent, 0);
	    }
	}
    }

	public void uploadResult() {
	Date date = new Date();
	//2010-05-28T17:40:25CST
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

	String xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	xml += "<result";
	xml += " apiKey=\"" + apiKey + "\"";
	xml += " benchmark=\"" + benchmarkName + "\"";
	xml += " executedTimestamp=\"" + sdf.format(date) + "\"";
	xml += ">";

	Case mycase;
	for (int i = 0; i < mCases.size(); i++) {
	    mycase = mCases.get(i);
	    xml += mycase.getXMLBenchmark();
	}

	xml += "</result>";
	MicroBenchmark.upload(xml, postUrl, apiKey, benchmarkName) ;
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

    private void writeToSDCard(String filename, String output) {
	File file = new File(SDCARD, filename);
	if (file.exists()) {
	    Log.i(TAG, "File exists, delete " + SDCARD + "/" + filename);
	    file.delete();
	}

	try {
	    file.createNewFile();
	    FileOutputStream fos = new FileOutputStream(file);
	    fos.write(output.getBytes());
	    fos.flush();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
