package com.mysaver.receivers;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mybitems.items.AppItem;
import com.mybitems.items.ErrorItem;
import com.mysaver.utils.Utils;

/**
 * Receiver that collects all the running apps on the device
 * 
 * @author Marcel Cunha
 * 
 */
public class AppReceiver extends BaseReceiver {
	private static final String TAG = "AppReceiver";
	private ActivityManager mManager;
	private static final int MAX_APPS = 10;
	private String mId;

	public AppReceiver(Context context) {
		super(context);

		try {
			mId = Utils.getId(context);
			Log.d(TAG, "Creating AppReceiver: " + mId);
			mManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
			mManager.getMemoryInfo(mInfo);
			saveSensor();
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void saveSensor() {
		Log.d(TAG, "saveSensor");
		AppItem item = null;
		try {
			item = new AppItem(mId);
			item.setApps(getApps());
			addItem(item);
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}
	}

	/** Return the list of apps running */
	private ArrayList<String> getApps() {
		Log.d(TAG, "getApps");
		ArrayList<String> list = null;
		try {
			list = new ArrayList<String>();
			List<RunningTaskInfo> listOfRunningProcess = mManager
					.getRunningTasks(MAX_APPS);

			Log.d(getClass().getSimpleName(), "Running process "
					+ listOfRunningProcess.size());
			int count = 0;
			for (RunningTaskInfo runningAppProcessInfo : listOfRunningProcess) {
				String name = runningAppProcessInfo.baseActivity
						.toShortString();
				String[] split = name.split("/");

				if (split != null && split[0] != null) {
					String finalName = split[0].replace("{", "");
					count += finalName.length();
					if (count < 400) {
						list.add(finalName);
					} else {
						count -= finalName.length();
					}

				}
			}
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());

			addItem(item);
			Log.e(TAG, e.toString());
		}
		return list;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Do nothing
	}
}
