package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Item representing the WiFi sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
public class WiFiItem extends BaseItem {

	/**
	 * These constants should be used while we do not have access to the correct
	 * android lib.
	 */
	private static final int CONNECTED = 2;
	private static final int CONNECTING = 5;
	/**
	 * [IDLE, SCANNING, CONNECTING, AUTHENTICATING, OBTAINING_IPADDR, CONNECTED,
	 * SUSPENDED, DISCONNECTING, DISCONNECTED, FAILED, BLOCKED,
	 * VERIFYING_POOR_LINK, CAPTIVE_PORTAL_CHECK]
	 */

	/** Indicate if the WiFi is connected or not */
	private int mConnectionState = -1;
	/** Indicate if the WiFi is enabled or disabled */
	private int mWiFiState = -1;
	/** Indicate if the WiFi is enabled or disabled */
	private int mWiFiPreviousState = -1;
	/** Indicate the WiFi MAC address */
	private String mMacAddress;
	/** Indicate the WiFi transmitted bytes */
	private long mTxBytes;
	/** Indicate the WiFi received bytes */
	private long mRxBytes;
	/** Indicate the WiFi sinal strength */
	private int mSinalStrength;
	/** Indicate the link speed */
	private int mLinkSpeed;
	/** Indicate the timestamp for the created object */
	private long mTimestamp;
	/** Indicate the id of the user */
	private String mId;

	public static final String TABLE_NAME = "wifiItem";

	public WiFiItem(String id) {
		mId = id;
		mTimestamp = System.currentTimeMillis();
	}

	@Override
	public int getType() {
		return ItemType.WIFI.ordinal();
	}

	public void setConnectionState(int mState) {
		this.mConnectionState = mState;
	}

	public void setWiFiState(int state) {
		this.mWiFiState = state;
	}

	public void setWiFiPreviousState(int state) {
		this.mWiFiPreviousState = state;
	}

	public String getMacAddress() {
		return mMacAddress;
	}

	public void setMacAddress(String mMacAddress) {
		this.mMacAddress = mMacAddress;
	}

	public long getTxBytes() {
		return mTxBytes;
	}

	public void setTxBytes(long mTxBytes) {
		this.mTxBytes = mTxBytes;
	}

	public long getRxBytes() {
		return mRxBytes;
	}

	public void setRxBytes(long mRxBytes) {
		this.mRxBytes = mRxBytes;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public void setLinkSpeed(int linkSpeed) {
		mLinkSpeed = linkSpeed;

	}

	public void setSinalStrenght(int ss) {
		mSinalStrength = ss;
	}

	public int getConnectionState() {
		return mConnectionState;
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimestamp);
		object.put(ApplicationConstants.connectionState, mConnectionState);
		object.put(ApplicationConstants.wifiState, mWiFiState);
		object.put(ApplicationConstants.wifiPreviousState, mWiFiPreviousState);
		object.put(ApplicationConstants.mac, mMacAddress);
		object.put(ApplicationConstants.rx, mRxBytes);
		object.put(ApplicationConstants.tx, mTxBytes);
		object.put(ApplicationConstants.sinalstrength, mSinalStrength);
		object.put(ApplicationConstants.linkspeed, mLinkSpeed);
		return object;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = object.getString(ApplicationConstants.id);
			mTimestamp = object.getLong(ApplicationConstants.timestamp);
			mConnectionState = object
					.getInt(ApplicationConstants.connectionState);
			mWiFiState = object.getInt(ApplicationConstants.wifiState);
			mWiFiPreviousState = object
					.getInt(ApplicationConstants.wifiPreviousState);
			mRxBytes = object.getLong(ApplicationConstants.rx);
			mTxBytes = object.getLong(ApplicationConstants.tx);
			mSinalStrength = object.getInt(ApplicationConstants.sinalstrength);
			mLinkSpeed = object.getInt(ApplicationConstants.linkspeed);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			mMacAddress = object.getString(ApplicationConstants.mac);
		} catch (JSONException jsone) {
			mMacAddress = "";
		}

	}

	/**
	 * This function is used only to check if the WIFI is ON and CONNECTED or
	 * CONNECTING.
	 */
	@Override
	public boolean isUsed() {
		if (mWiFiState == 3) {
			if (mConnectionState == CONNECTED || mConnectionState == CONNECTING) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mConnectionState + ","
				+ mWiFiState + "," + mWiFiPreviousState + ",'" + mMacAddress
				+ "'," + mLinkSpeed + "," + mSinalStrength + "," + mTxBytes
				+ "," + mRxBytes + "," + mTimestamp + ")";
		return insert;
	}
}
