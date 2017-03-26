package com.mysaver.receivers;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mybitems.items.BaseItem;
import com.mysaver.persistence.DatabaseManager;

/**
 * Base class that handles the list of objects to save on local database
 * 
 * @author Marcel Cunha
 */
public abstract class BaseReceiver extends BroadcastReceiver {

	private ArrayList<BaseItem> mData;

	private Context mContext;

	private static final String TAG = "BaseReceiver";

	private static final int LOCAL_MAX_SIZE = 2;

	public BaseReceiver(Context context) {
		mContext = context;
		mData = new ArrayList<BaseItem>();
	}

	/**
	 * Method to be implemented on the children class. Create specific items and
	 * add them and call the addItem.
	 */
	public abstract void saveSensor();

	/**
	 * Add the item on the mData list. This function should be synchronized
	 * because many instances of this class are trying to access the same array
	 * list.
	 */
	public synchronized void addItem(BaseItem item) {
		Log.d(TAG + item.getType(), "Adding item : " + item.getType());
		if (item != null && mData != null) {
			Log.d(TAG, "Entry..");
			mData.add(item);

			if (mData.size() > LOCAL_MAX_SIZE) {
				saveData();
			}

			Log.d(TAG + item.getType(), "Finish adding item : " + mData.size());
		}

	}

	/** Save the data on the local Database */
	private synchronized void saveData() {
		try {
			if (mData != null && mData.size() > LOCAL_MAX_SIZE) {
				DbTask task = new DbTask();
				task.execute();
			}

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	private class DbTask extends AsyncTask<Void, Integer, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			Log.d(TAG, "Executing task..");
			long insert = -1;
			if (mData != null && mData.size() > 0) {
				ArrayList<BaseItem> dataCopy = new ArrayList<BaseItem>(mData);
				mData.clear();
				DatabaseManager manager = DatabaseManager.getInstance(mContext);
				if (manager != null && dataCopy != null && !dataCopy.isEmpty()) {
					insert = manager.insert(dataCopy);
					Log.d(TAG, "Inserted : " + insert);
					if (insert == -1) {
						mData.addAll(dataCopy);
					}

				}
			}
			return insert;
		}
	}
}
