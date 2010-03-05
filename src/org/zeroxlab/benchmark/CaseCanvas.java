package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import java.nio.*;

public class CaseCanvas extends Case{

    CaseCanvas() {
	super("CaseCanvas", CanvasTester.getFullClassName(), 3);
    }

    public String getDescription() {
	return "Hi";
    }
}
