package org.zeroxlab.benchmark;

import org.zeroxlab.benchmark.MicroBenchmark;

import android.util.Log;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;

import android.app.Dialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.os.SystemClock;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;

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
    String mFailMsg;

    MicroBenchmark mb;

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
        showDialog(0);
        String attr = "";
        attr += " apiKey=\"" + mAPIKey.getText().toString() + "\"";
        attr += " benchmark=\"" + mBenchName.getText().toString() + "\"";
        StringBuffer _mXML = new StringBuffer(mXML);
        int _index = _mXML.indexOf("result") + 6;
        _mXML.insert(_index, attr);
        Log.e("bzlog", _mXML.toString());

        mURL = "http://" + mAppSpot.getText().toString() + ".appspot.com:80/run/";
        mb = new MicroBenchmark(_mXML.toString(), mURL, mAPIKey.getText().toString(), mBenchName.getText().toString(), handler) ;
        mb.start();
    }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int state = msg.getData().getInt(MicroBenchmark.STATE);
            Log.e("bzlog", "check: " + state);
            if (state != MicroBenchmark.RUNNING) {
                dismissDialog(0);
                if (state == MicroBenchmark.DONE) {
                    showDialog(1);
                }
                else {
                    showDialog(2);
                }
                Log.e("bzlog", msg.getData().getString(MicroBenchmark.MSG));
            }
        }
    };

    protected Dialog onCreateDialog(int id) {
    switch(id) {
        case(0):
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading, please wait...");
            return dialog;
        case(1):
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Upload complete.")
                   .setCancelable(false)
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                       }
                   });
            return builder.create();
        case(2):
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setMessage("Upload failed.")
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                       }
                   });
            return builder2.create();
        default:
            return null;
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
