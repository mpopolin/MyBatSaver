package com.mysaver.receivers;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mybitems.items.BluetoothItem;
import com.mybitems.items.ErrorItem;
import com.mysaver.utils.Utils;

/**
 * Receiver that collects data related to device's bluetooth
 * 
 * @author Marcel Cunha
 * 
 */
public class BluetoothReceiver extends BaseReceiver {

	private boolean DOLOG = true;

	private static final int NO_STATE = -1;

	private String TAG = "BluetoothReceiver";

	/* The Bluetooh adapter used dto get info about it. */
	private BluetoothAdapter mAdapter = null;

	/* Indicate the actual state */
	private int mActualState = NO_STATE;

	/* Indicate the previous state */
	private int mPreviousState = NO_STATE;

	/* Indicate if its connected or not */
	private int mActualConnectionState = NO_STATE;

	/* Indicate if its connected or not */
	private int mPreviousConnectionState = NO_STATE;

	/* Indicate the number of connections */
	private int mConnections = 0;

	/* Indicate the user ID */
	private String mId;

	public BluetoothReceiver(Context context) {
		super(context);
		mId = Utils.getId(context);
		log("Creating BluetoothReceiver");
		try {
			mAdapter = BluetoothAdapter.getDefaultAdapter();
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}
	}

	public final int getActualState() {
		return mActualState;
	}

	public final int getPreviousState() {
		return mPreviousState;
	}

	public final boolean isActive() {
		if (mActualState == BluetoothAdapter.STATE_ON) {
			return true;
		}
		return false;
	}

	public final boolean isConnected() {
		if (isActive()
				&& mActualConnectionState == BluetoothAdapter.STATE_CONNECTED) {
			return true;
		}
		return false;
	}

	private void log(String s) {
		if (DOLOG) {
			Log.d(TAG, s);
		}
	}

	@Override
	public void onReceive(Context context, Intent paramIntent) {
		log("onReceive");

		try {
			String str = paramIntent.getAction();
			log(str);
			if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(str)) {

				// Get the actual state
				mActualConnectionState = paramIntent.getIntExtra(
						BluetoothAdapter.EXTRA_CONNECTION_STATE, NO_STATE);

				// Get the previous state
				mPreviousConnectionState = paramIntent.getIntExtra(
						BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE,
						NO_STATE);
				log("Actual Connection state : " + mActualConnectionState);
				log("Previous Connection state : " + mPreviousConnectionState);

			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(str)) {
				mActualState = paramIntent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, NO_STATE);
				mPreviousState = paramIntent.getIntExtra(
						BluetoothAdapter.EXTRA_PREVIOUS_STATE, NO_STATE);

				log("Actual state : " + mActualState);
				log("Previous state : " + mPreviousState);
			}

			// Get the number of paired devices
			Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
			if (pairedDevices != null) {
				mConnections = pairedDevices.size();
			}

			saveSensor();
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void saveSensor() {
		Log.d(TAG, "saveSensor");
		BluetoothItem item = null;
		try {
			item = new BluetoothItem(mId);
			item.setActualConnectionState(mActualConnectionState);
			item.setPreviousConnectionState(mPreviousConnectionState);
			item.setState(mActualState);
			item.setPreviousState(mPreviousState);
			item.setConnections(mConnections);
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			item = null;
			Log.e(TAG, e.toString());
		}
		addItem(item);
	}

}