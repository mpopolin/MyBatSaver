package com.mysaver.receivers;

import java.util.ArrayList;

import android.content.Context;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mybitems.items.BaseItem;
import com.mybitems.items.ErrorItem;
import com.mybitems.items.TelItem;
import com.mysaver.persistence.DatabaseManager;
import com.mysaver.utils.Utils;

/**
 * Receiver for the Mobile sensor. This class does not follow the standard of
 * extending the BaseReceiver, because it has to extends PhoneStateListener.
 * 
 * @author Marcel Cunha
 * 
 */
public class MobileReceiver extends PhoneStateListener {

	private TelephonyManager mManager;
	private Context mContext;
	private static final String TAG = "MobileReceiver";

	private static final int MAX = 30;

	// Used to create the item.
	private int mNetworkType;
	private int mDataState;
	private int mDataActivity;
	private int mCallState;
	private SignalStrength mSignalStrenght;
	private long mTxBytes;
	private long mRxBytes;
	private String mId;

	private ArrayList<BaseItem> mData = new ArrayList<BaseItem>();

	public MobileReceiver(Context context) {
		try {
			Log.d(TAG, "Creating MobileReceiver");
			mContext = context;
			mId = Utils.getId(context);
			mManager = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);

			mManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			mManager.listen(this, PhoneStateListener.LISTEN_DATA_ACTIVITY);
			mManager.listen(this,
					PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
			mManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);

			// Create the first item, since we do not know if we will receive
			// any changed notification to update its values
			mNetworkType = mManager.getNetworkType();
			mDataState = mManager.getDataState();
			mDataActivity = mManager.getDataActivity();
			mCallState = mManager.getCallState();

			// As we cannot get the Signal strength the first time we create
			// this object, we need to wait the callback.
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}
	}

	private TelItem setItem() {
		Log.d(TAG, "setItem");

		TelItem item = null;
		try {
			mTxBytes = TrafficStats.getMobileTxBytes();
			mRxBytes = TrafficStats.getMobileRxBytes();
			item = new TelItem(mId);
			item.setmCallState(mCallState);
			item.setmDataActivity(mDataActivity);
			item.setmDataState(mDataState);
			item.setmNetworkType(mNetworkType);
			// item.setmSignalStrenght(mSignalStrenght);
			item.setmRxBytes(mRxBytes);
			item.setmTxBytes(mTxBytes);
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}

		return item;

	}

	public void saveSensor() {
		Log.d(TAG, "saveSensor");

		mData.add(setItem());
		try {
			if (mData.size() > MAX) {

				DbTask task = new DbTask();
				task.execute();
			}
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onDataConnectionStateChanged(int state, int networkType) {
		Log.d(TAG, "dataChanged" + state + " " + networkType);
		try {
			mNetworkType = networkType;
			mDataState = state;
			mData.add(setItem());
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}
		super.onDataConnectionStateChanged(state, networkType);
	}

	@Override
	public void onDataActivity(int direction) {
		Log.d(TAG, "onDataActiivty " + direction);
		try {
			mDataActivity = direction;
			mData.add(setItem());
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}
		super.onDataActivity(direction);
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		try {
			mSignalStrenght = signalStrength;
			mData.add(setItem());
			Log.d(getClass().getSimpleName(), "onSinalStrenght : "
					+ signalStrength);
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}
		super.onSignalStrengthsChanged(signalStrength);
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		Log.d(getClass().getSimpleName(), "onCallStateChanged : " + state + " "
				+ incomingNumber);
		try {
			mCallState = state;
			mData.add(setItem());
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			mData.add(eitem);
			Log.e(TAG, e.toString());
		}
		super.onCallStateChanged(state, incomingNumber);
	}

	private class DbTask extends AsyncTask<Void, Integer, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			Log.d(TAG, "Executing task..");
			long insert = -1;
			if (mData != null) {
				ArrayList<BaseItem> dataCopy = new ArrayList<BaseItem>(mData);
				DatabaseManager manager = DatabaseManager.getInstance(mContext);
				if (manager != null) {
					insert = manager.insert(dataCopy);
					if (insert != -1) {
						mData.clear();
					}
				}
			}
			return insert;
		}
	}
}
