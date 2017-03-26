package com.mysaver.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.mybsaver.R;
import com.mysaver.service.LoggerService;

/**
 * Main class responsible for creating and connecting the service that will
 * handle the logger functionality.
 * 
 * @author Marcel Cunha
 * 
 */
public class MainActivity extends Activity implements ServiceConnection {

	public static final String LOG_ON_SEVER = "server";

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		try {
			// Use this to start and trigger a service
			Intent it = new Intent(this, LoggerService.class);
			startService(it);
			bindService(it, this, Context.BIND_AUTO_CREATE);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		try {
			unbindService(this);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onServiceConnected(final ComponentName name,
			final IBinder service) {

		Log.d(TAG, "serviceConnected");
		try {

			// Start the service components
			// startSource();
			Toast.makeText(getApplicationContext(), "Service connected.",
					Toast.LENGTH_SHORT).show();

			// As the service is already started, finish this activity.
			this.finish();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onServiceDisconnected(final ComponentName name) {
		Log.d(TAG, "Service disconnected");

	}

}
