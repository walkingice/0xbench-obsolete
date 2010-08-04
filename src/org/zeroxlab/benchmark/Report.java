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
