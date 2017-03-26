package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base item used to indicate the errors that occurred during the program
 * execution on the server
 * 
 * @author Marcel Cunha
 */
public class ErrorItem extends BaseItem {

	/** Indicate the error message */
	private String mError;

	/** Indicate the id */
	private String mId;

	/** Indicate the timestamp */
	private long mTimestamp;

	public static final String TABLE_NAME = "errorItem";

	public ErrorItem(String id, String error) {
		mError = error;
		mId = id;
		mTimestamp = System.currentTimeMillis();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimestamp);
		object.put(ApplicationConstants.error, mError);
		return object;
	}

	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	public String getErrorMessage() {
		return mError;
	}

	@Override
	public int getType() {
		return ItemType.ERROR.ordinal();
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = (object.getString(ApplicationConstants.id));
			mTimestamp = (object.getLong(ApplicationConstants.timestamp));
			mError = (object.getString(ApplicationConstants.error));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Error items are always used by default
	 */
	@Override
	public boolean isUsed() {
		return true;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "','" + mError + "'," + mTimestamp + ")";
		return insert;
	}

}
