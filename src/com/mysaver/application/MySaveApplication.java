package com.mysaver.application;

import com.mysaver.persistence.DatabaseManager;
import com.mysaver.utils.Utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Application class that holds some important information.
 * 
 * @author Marcel Cunha
 * */
public class MySaveApplication extends Application {

	private static volatile Context sContext;

	private static final String TAG = "MySaveApplication";

	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate app");
		setAppContext(this);
		Utils.getId(getContext());
		DatabaseManager.getInstance(getContext());
	}

	public static Context getContext() {
		return sContext;
	}

	private void setAppContext(final Context context) {
		sContext = context;
	}

}