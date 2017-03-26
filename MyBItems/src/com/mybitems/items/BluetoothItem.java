package com.mybitems.items;

/**
 * Item representing the Bluetooth sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;

public class BluetoothItem extends BaseItem {

	/** Indicate the bluetooth state */
	private int mState;
	/** Indicate number of connections */
	private int mConnections;
	/** Indicate the bluetooth previous state */
	private int mPreviousState;
	/** Indicate the bluetooth connection state */
	private int mActualConnectionState;
	/** Indicate the previous bluetooth connection state */
	private int mPreviousConnectionState;
	/** Indicate the timestamp for the created object */
	private long mTimestamp;
	/** Indicate the id of the user */
	private String mId;

	public static final String TABLE_NAME = "bluetoothItem";

	public int getState() {
		return mState;
	}

	@Override
	public int getType() {
		return ItemType.BLUETOOTH.ordinal();
	}

	public void setState(int mState) {
		this.mState = mState;
	}

	public int getConnections() {
		return mConnections;
	}

	public void setConnections(int mConnections) {
		this.mConnections = mConnections;
	}

	public int getPreviousState() {
		return mPreviousState;
	}

	public void setPreviousState(int mPreviousState) {
		this.mPreviousState = mPreviousState;
	}

	public int getActualConnectionState() {
		return mActualConnectionState;
	}

	public void setActualConnectionState(int mConnectionState) {
		this.mActualConnectionState = mConnectionState;
	}

	public int getPreviousConnectionState() {
		return mPreviousConnectionState;
	}

	public void setPreviousConnectionState(int mConnectionState) {
		this.mPreviousConnectionState = mConnectionState;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public BluetoothItem(String id) {
		mId = id;
		mTimestamp = System.currentTimeMillis();
	}

	public void setId(String id) {
		mId = id;
	}

	/**
	 * Check if the Bluetooth item created was being used. This means that the
	 * adapter should be ON and CONNECTED, or at least CONNECTING. Otherwise, it
	 * could be either OFF or ON, but not used. It should not occur, but we are
	 * also checking the number of connections, so, it there wasn't any
	 * connections, we can consider it as not used.
	 * 
	 * @return boolean indicating if the item was created by a used state of the
	 *         adapter
	 */
	public boolean isUsed() {
		if (mState == BluetoothAdapter.STATE_ON) {
			if (mActualConnectionState == BluetoothAdapter.STATE_CONNECTED
					|| mActualConnectionState == BluetoothAdapter.STATE_CONNECTING) {
				if (mConnections > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimestamp);
		object.put(ApplicationConstants.state, mState);
		object.put(ApplicationConstants.connections, mConnections);
		object.put(ApplicationConstants.previousstate, mPreviousState);
		object.put(ApplicationConstants.actualconnectionstate,
				mActualConnectionState);
		object.put(ApplicationConstants.previousconnectionstate,
				mActualConnectionState);
		return object;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = (object.getString(ApplicationConstants.id));
			mTimestamp = (object.getLong(ApplicationConstants.timestamp));
			mState = (object.getInt(ApplicationConstants.state));
			mConnections = (object.getInt(ApplicationConstants.connections));
			mPreviousState = (object.getInt(ApplicationConstants.previousstate));
			mActualConnectionState = (object
					.getInt(ApplicationConstants.actualconnectionstate));
			mPreviousConnectionState = (object
					.getInt(ApplicationConstants.previousconnectionstate));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mState + "," + mConnections + ","
				+ mPreviousState + "," + mActualConnectionState + ","
				+ mPreviousConnectionState + "," + mTimestamp + ")";

		return insert;
	}
}
