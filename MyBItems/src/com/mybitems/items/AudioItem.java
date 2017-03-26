package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Item representing the audio sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
public class AudioItem extends BaseItem {

	/** Indicate audio mode */
	private int mMode;
	/** Indicate if the speaker is on */
	private boolean mSpeakerOn;
	/** Indicate if the music is on */
	private boolean musicOn;
	/** Indicate the music volume */
	private int musicVolume;
	/** Indicate the call volume */
	private int callVolume;
	/** Indicate the ring volume */
	private int ringVolume;
	/** Indicate the timestamp for the created object */
	private long mTimestamp;
	/** Indicate the id of the user */
	private String mId;

	public static final String TABLE_NAME = "audioItem";

	public AudioItem(String id) {
		mId = id;
		mTimestamp = System.currentTimeMillis();
	}

	public int getmMode() {
		return mMode;
	}

	public boolean isSpeakerOn() {
		return mSpeakerOn;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	public boolean isMusicOn() {
		return musicOn;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public int getCallVolume() {
		return callVolume;
	}

	@Override
	public int getType() {
		return ItemType.AUDIO.ordinal();
	}

	public int getRingVolume() {
		return ringVolume;
	}

	public long getmTimeStamp() {
		return mTimestamp;
	}

	public void setSpeakerOn(boolean speakerOn) {
		this.mSpeakerOn = speakerOn;
	}

	public void setMode(int mMode) {
		this.mMode = mMode;
	}

	public void setMusicOn(boolean musicOn) {
		this.musicOn = musicOn;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}

	public void setCallVolume(int callVolume) {
		this.callVolume = callVolume;
	}

	public void setRingVolume(int ringVolume) {
		this.ringVolume = ringVolume;
	}

	public void setmTimeStamp(long mTimeStamp) {
		this.mTimestamp = mTimeStamp;
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimestamp);
		object.put(ApplicationConstants.mode, mMode);
		object.put(ApplicationConstants.musicVolume, musicVolume);
		object.put(ApplicationConstants.musicPlaying, musicOn);
		object.put(ApplicationConstants.ring, ringVolume);
		object.put(ApplicationConstants.speaker, mSpeakerOn);
		object.put(ApplicationConstants.callVolume, callVolume);

		return object;

	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = object.getString(ApplicationConstants.id);
			mTimestamp = object.getLong(ApplicationConstants.timestamp);
			mMode = object.getInt(ApplicationConstants.mode);
			musicVolume = object.getInt(ApplicationConstants.musicVolume);
			musicOn = object.getBoolean(ApplicationConstants.musicPlaying);
			ringVolume = object.getInt(ApplicationConstants.ring);
			mSpeakerOn = object.getBoolean(ApplicationConstants.speaker);
			callVolume = object.getInt(ApplicationConstants.callVolume);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	/**
	 * We will consider that the audio was being used when some music was being played. 
	 * Otherwise, can consider that the phone was in IDLE state, so we can make some averages to determine the rest of values.
	 */
	public boolean isUsed() {
		return musicOn;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mSpeakerOn + "," + mMode + ","
				+ musicOn + "," + musicVolume + "," + callVolume + ","
				+ ringVolume + "," + mTimestamp + ")";
		return insert;
	}
}
