package org.zeroxlab.benchmark;

import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.*;
import java.nio.*;

import java.util.LinkedList;

/* Construct a basic UI */
public class Report extends Activity {

    public final static String TAG = "Repord";
    public final static String REPORT = "REPORT";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.report);

        mTextView = (TextView)findViewById(R.id.report_text);

        Intent intent = getIntent();
        String report = intent.getStringExtra(REPORT);

        if (report == null || report.equals("")) {
            mTextView.setText("oooops...report not found");
        } else {
            mTextView.setText(report);
        }
    }

    public static String fullClassName() {
        return "org.zeroxlab.benchmark.Report";
    }

    public static String packageName() {
        return "org.zeroxlab.benchmark";
    }
}
