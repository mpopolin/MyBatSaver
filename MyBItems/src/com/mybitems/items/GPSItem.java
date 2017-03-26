package com.mybitems.items;

/**
 * Item representing the GPS sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
import org.json.JSONException;
import org.json.JSONObject;

public class GPSItem extends BaseItem {

	/** Indicate the GPS status */
	private int mState;

	/** Indicate the timestamp for the created object */
	private long mTimeStamp;

	/** Indicate the id of the user */
	private String mId;

	public static final String TABLE_NAME = "gpsItem";

	public long getmTimeStamp() {
		return mTimeStamp;
	}

	public void setTimestamp(long timestamp) {
		this.mTimeStamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return mTimeStamp;
	}

	@Override
	public int getType() {
		return ItemType.GPS.ordinal();
	}

	public void setmTimeStamp(long mTimeStamp) {
		this.mTimeStamp = mTimeStamp;
	}

	public int getmState() {
		return mState;
	}

	public void setmState(int mState) {
		this.mState = mState;
	}

	public void setId(String id) {
		mId = id;
	}

	public GPSItem(String id) {
		mId = id;
		mTimeStamp = System.currentTimeMillis();
	}

	@Override
	/**
	 * If the event is the GPS was started or if we receive any update from the satellites,
	 *  this means that the GPS was being used.
	 */
	public boolean isUsed() {
		if (mState == 1 || mState == 4) {
			return true;
		}
		return false;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = (object.getString(ApplicationConstants.id));
			mTimeStamp = (object.getLong(ApplicationConstants.timestamp));
			mState = (object.getInt(ApplicationConstants.state));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimeStamp);
		object.put(ApplicationConstants.state, mState);

		return object;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mState + "," + mTimeStamp + ")";
		return insert;
	}

}
