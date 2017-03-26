package com.mysaver.receivers;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mysaver.activity.MainActivity;

/**
 * Receiver responsible for starting the Service when the device boots.
 * 
 * @author Marcel Cunha
 * 
 */
public class StartReceiver extends BroadcastReceiver {

	private String TAG = "StartReceiver";

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(TAG, "onReceive");
		Intent pushIntent = new Intent(context, MainActivity.class);
		pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(pushIntent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, e.toString());
		}
	}
}
