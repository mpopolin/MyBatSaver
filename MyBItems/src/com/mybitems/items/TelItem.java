package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Item representing the Telephony sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
public class TelItem extends BaseItem {

	/** Indicate if the network type */
	private int mNetworkType;
	/** Indicate the data state */
	private int mDataState;
	/** Indicate the data activity */
	private int mDataActivity;
	/** Indicate the telephony call state */
	private int mCallState;
	/** Indicate the signal strength */
	// private SignalStrength mSignalStrength;
	/**
	 * As we cannot instantiate a new SignalStrength object, we should use this
	 * int just to represent the GSM
	 */
	private int mSignalStrengthValue;
	/** Indicate the time that the object was created */
	private long mTimeStamp;
	/** Indicate the transferred bytes */
	private long mTxBytes;
	/** Indicate the received bytes */
	private long mRxBytes;
	/** Indicate the user id */
	private String mId;

	public static final String TABLE_NAME = "telItem";

	public long getmTxBytes() {
		return mTxBytes;
	}

	public void setmTxBytes(long mTxBytes) {
		this.mTxBytes = mTxBytes;
	}

	public void setTimestamp(long timestamp) {
		this.mTimeStamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return mTimeStamp;
	}

	public long getmRxBytes() {
		return mRxBytes;
	}

	public void setSignalStrengthValue(int value) {
		this.mSignalStrengthValue = value;
	}

	public void setmRxBytes(long mRxBytes) {
		this.mRxBytes = mRxBytes;
	}

	public TelItem(String id) {
		mId = id;
		mTimeStamp = System.currentTimeMillis();
	}

	@Override
	public int getType() {
		return ItemType.TEL.ordinal();
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimeStamp);
		object.put(ApplicationConstants.state, mDataState);
		object.put(ApplicationConstants.activity, mDataActivity);
		object.put(ApplicationConstants.nettype, mNetworkType);
		object.put(ApplicationConstants.callstate, mCallState);
		object.put(ApplicationConstants.rx, mRxBytes);
		object.put(ApplicationConstants.tx, mTxBytes);
		object.put(ApplicationConstants.signalstrength, mSignalStrengthValue);

		// if (mSignalStrength != null) {
		// if (mSignalStrength.isGsm()) {
		// object.put(ApplicationConstants.signalstrength,
		// mSignalStrength.getGsmSignalStrength());
		// } else {
		// // TODO: Is no gms
		// object.put(ApplicationConstants.signalstrength, -1);
		//
		// }
		// } else {
		// object.put(ApplicationConstants.signalstrength, -1);
		//
		// }

		return object;

	}

	public int getmNetworkType() {
		return mNetworkType;
	}

	public void setmNetworkType(int mNetworkType) {
		this.mNetworkType = mNetworkType;
	}

	public int getmDataState() {
		return mDataState;
	}

	public void setmDataState(int mDataState) {
		this.mDataState = mDataState;
	}

	public int getmDataActivity() {
		return mDataActivity;
	}

	public void setmDataActivity(int mDataActivity) {
		this.mDataActivity = mDataActivity;
	}

	public int getmCallState() {
		return mCallState;
	}

	public void setmCallState(int mCallState) {
		this.mCallState = mCallState;
	}

	// public SignalStrength getmSignalStrenght() {
	// return mSignalStrength;
	// }
	//
	// public void setmSignalStrenght(SignalStrength mSignalStrenght) {
	// this.mSignalStrength = mSignalStrenght;
	// }

	public int getSignalStrenghtValue() {
		return mSignalStrengthValue;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {

			mNetworkType = object.getInt(ApplicationConstants.nettype);
			mDataActivity = object.getInt(ApplicationConstants.activity);
			mDataState = object.getInt(ApplicationConstants.state);
			mCallState = object.getInt(ApplicationConstants.callstate);
			mSignalStrengthValue = object
					.getInt(ApplicationConstants.signalstrength);
			mTimeStamp = object.getLong(ApplicationConstants.timestamp);
			mTxBytes = object.getLong(ApplicationConstants.tx);
			mRxBytes = object.getLong(ApplicationConstants.rx);
			mId = object.getString(ApplicationConstants.id);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	/** This will consider that the MOBILE TRAFFIC as USED when the adapter 
	 * is CONNECTED or CONNECTING and there is some data activity IN, OUT or BOTH */
	public boolean isUsed() {
		if (mDataState == 2 || mDataState == 1) {
			if (mDataActivity == 1 || mDataActivity == 2 || mDataActivity == 3) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mNetworkType + "," + mDataState
				+ "," + mDataActivity + "," + mCallState + ","
				+ mSignalStrengthValue + "," + mTxBytes + "," + mRxBytes + ","
				+ mTimeStamp + ")";
		return insert;
	}
}
