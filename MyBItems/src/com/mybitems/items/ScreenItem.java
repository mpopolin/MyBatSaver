package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Item representing the screen sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
public class ScreenItem extends BaseItem {

	/** Indicate the screen height */
	private float mHeight;
	/** Indicate the screen width */
	private float mWidth;
	/** Indicate the screen state */
	private int mScreenState;
	/** Indicate the screen brightness */
	private int mBrightness;
	/** Indicate the screen refresh rate */
	private float mRefreshRate;
	/** Indicate the screen orientation */
	private int mOrientation;
	/** Indicate the timestamp for the created object */
	private long mTimestamp;
	/** Indicate the id of the user */
	private String mId;

	public static final String TABLE_NAME = "screenItem";

	public ScreenItem(String id) {
		mId = id;
		mTimestamp = System.currentTimeMillis();
	}

	public float getHeight() {
		return mHeight;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}

	public void setHeight(float mHeight) {
		this.mHeight = mHeight;
	}

	public float getWidth() {
		return mWidth;
	}

	@Override
	public int getType() {
		return ItemType.SCREEN.ordinal();
	}

	public void setWidth(float mWidth) {
		this.mWidth = mWidth;
	}

	public int getScreenState() {
		return mScreenState;
	}

	public void setScreenState(int mScreenState) {
		this.mScreenState = mScreenState;
	}

	public int getBrightness() {
		return mBrightness;
	}

	public void setBrightness(int mBrightness) {
		this.mBrightness = mBrightness;
	}

	public float getRefreshRate() {
		return mRefreshRate;
	}

	public void setRefreshRate(float mRefreshRate) {
		this.mRefreshRate = mRefreshRate;
	}

	public int getOrientation() {
		return mOrientation;
	}

	public void setOrientation(int mOrientation) {
		this.mOrientation = mOrientation;
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimestamp);
		object.put(ApplicationConstants.state, mScreenState);
		object.put(ApplicationConstants.width, mHeight);
		object.put(ApplicationConstants.height, mWidth);
		object.put(ApplicationConstants.brigthness, mBrightness);
		object.put(ApplicationConstants.orientation, mOrientation);
		object.put(ApplicationConstants.refresh, mRefreshRate);
		return object;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = object.getString(ApplicationConstants.id);
			mTimestamp = object.getLong(ApplicationConstants.timestamp);
			mScreenState = object.getInt(ApplicationConstants.state);
			mWidth = object.getInt(ApplicationConstants.width);
			mHeight = object.getInt(ApplicationConstants.height);
			mBrightness = object.getInt(ApplicationConstants.brigthness);
			mOrientation = object.getInt(ApplicationConstants.orientation);
			mRefreshRate = object.getInt(ApplicationConstants.refresh);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isUsed() {
		if (mScreenState == 1) {
			return true;
		}
		return false;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mHeight + "," + mWidth + ","
				+ mScreenState + "," + mBrightness + "," + mRefreshRate + ","
				+ mOrientation + "," + mTimestamp + ")";
		return insert;
	}

}
