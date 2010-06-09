package org.zeroxlab.benchmark;

import android.util.Log;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

public class Upload extends Activity implements View.OnClickListener {

    public final static String TAG = "Upload";
    public final static String XML = "XML";

    EditText mBenchName;
    EditText mAppSpot;
    EditText mEmail;
    EditText mAPIKey;
    Button mSend;

    String mURL;
    String mXML;

    @Override
    protected void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	setContentView(R.layout.upload);

    mBenchName = (EditText)findViewById(R.id.benchName);
    mAppSpot   = (EditText)findViewById(R.id.appSpot);
    mEmail     = (EditText)findViewById(R.id.email);
    mAPIKey    = (EditText)findViewById(R.id.api);

    mSend      = (Button)findViewById(R.id.send);
    mSend.setOnClickListener(this);

	Intent intent = getIntent();
	mXML = intent.getStringExtra(XML);

    }

    public void onClick(View v) {
    if (v == mSend) {
        String attr = "";
        attr += " apiKey=\"" + mAPIKey.getText().toString() + "\"";
        attr += " benchmark=\"" + mBenchName.getText().toString() + "\"";
        StringBuffer _mXML = new StringBuffer(mXML);
        int _index = _mXML.indexOf("result") + 6;
        _mXML.insert(_index, attr);
        Log.e("bzlog", _mXML.toString());

        mURL = "http://" + mAppSpot.getText().toString() + ".appspot.com:80/run/";
        MicroBenchmark.upload(_mXML.toString(), mURL, mAPIKey.getText().toString(), mBenchName.getText().toString()) ;
    }
    }

    @Override
    protected void onResume() {
    super.onResume();

    SharedPreferences prefs = getPreferences(0); 
    String restoredText;
    restoredText = prefs.getString("mBenchName", null);
    if (restoredText != null) 
        mBenchName.setText(restoredText, TextView.BufferType.EDITABLE);
    restoredText = prefs.getString("mAppSpot", null);
    if (restoredText != null) 
        mAppSpot.setText(restoredText, TextView.BufferType.EDITABLE);
    restoredText = prefs.getString("mEmail", null);
    if (restoredText != null) 
        mEmail.setText(restoredText, TextView.BufferType.EDITABLE);
    restoredText = prefs.getString("mAPIKey", null);
    if (restoredText != null) 
        mAPIKey.setText(restoredText, TextView.BufferType.EDITABLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("mBenchName",mBenchName.getText().toString());
        editor.putString("mAppSpot",mAppSpot.getText().toString());
        editor.putString("mEmail",mEmail.getText().toString());
        editor.putString("mAPIKey",mAPIKey.getText().toString());
        editor.commit();
    }

    public static String fullClassName() {
	return "org.zeroxlab.benchmark.Upload";
    }

    public static String packageName() {
	return "org.zeroxlab.benchmark";
    }

}
