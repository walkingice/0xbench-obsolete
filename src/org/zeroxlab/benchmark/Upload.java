/*
 * Copyright (C) 2010 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.widget.CheckBox;
import android.widget.Toast;
import android.view.View;

import android.text.TextWatcher;
import android.text.Editable;

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
    EditText mEmail;
    EditText mAPIKey;
    Button mSend;
    CheckBox mLogin;

    String mURL;
    String mXML;
    String mFailMsg;

    MicroBenchmark mb;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.upload);

        mLogin = (CheckBox)findViewById(R.id.login);
        mLogin.setChecked(false);
        mLogin.setOnClickListener(this);

        mBenchName = (EditText)findViewById(R.id.benchName);
        mEmail     = (EditText)findViewById(R.id.email);
        mAPIKey    = (EditText)findViewById(R.id.api);
        mBenchName.setEnabled(false);
        mEmail.setEnabled(false);
        mAPIKey.setEnabled(false);

        mSend      = (Button)findViewById(R.id.send);
        mSend.setOnClickListener(this);

        Intent intent = getIntent();
        mXML = intent.getStringExtra(XML);
    }

    public void onClick(View v) {
        Log.i(TAG, "onclick listener");
        if (v == mSend) {
            showDialog(0);

            StringBuffer _mXML;
            int _index;
            String attr;

            String benchName = getString(R.string.default_benchname);
            String apiKey = getString(R.string.default_api);
            String eMail = getString(R.string.default_email);
            if (mLogin.isChecked()) {
                benchName = mBenchName.getText().toString();
                apiKey = mAPIKey.getText().toString();
                eMail = mEmail.getText().toString();
            }

            attr = "";
            attr += " apiKey=\"" + apiKey + "\"";
            attr += " benchmark=\"" + benchName + "\"";
            _mXML = new StringBuffer(mXML);
            _index = _mXML.indexOf("result") + 6;
            _mXML.insert(_index, attr);
            Log.e(TAG, _mXML.toString());

            mURL = "http://" + getString(R.string.default_appspot) + ".appspot.com:80/run/";
            mb = new MicroBenchmark(_mXML.toString(), mURL, apiKey, benchName, handler) ;
            mb.start();
        } else if (v == mLogin) {
            if(mLogin.isChecked()) {
                mBenchName.setEnabled(true);
                mEmail.setEnabled(true);
                mAPIKey.setEnabled(true);
            } else {
                mBenchName.setEnabled(false);
                mEmail.setEnabled(false);
                mAPIKey.setEnabled(false);
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int state = msg.getData().getInt(MicroBenchmark.STATE);
            if (state != MicroBenchmark.RUNNING) {
                try {
                    dismissDialog(0);
                    removeDialog(0);
                } catch (Exception e) {
                }
                if (state == MicroBenchmark.DONE) {
                    showDialog(3);
                    showDialog(1);
                }
                else {
                    showDialog(2);
                }
                Log.e(TAG, msg.getData().getString(MicroBenchmark.MSG));
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
            case(3):
                String benchName = getString(R.string.default_benchname);
                String apiKey = getString(R.string.default_api);
                String eMail = getString(R.string.default_email);
                if (mLogin.isChecked()) {
                    benchName = mBenchName.getText().toString();
                    apiKey = mAPIKey.getText().toString();
                    eMail = mEmail.getText().toString();
                }
                String url = "http://" + getString(R.string.default_appspot) + ".appspot.com:80/run/" + eMail + "/" + benchName;

                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setMessage( url )
                        .setTitle("Result URL")
                        .setPositiveButton("OK", null)
                ;
                return builder3.create();
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
