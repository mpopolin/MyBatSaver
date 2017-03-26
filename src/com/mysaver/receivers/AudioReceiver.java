package com.mysaver.receivers;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.mybitems.items.AudioItem;
import com.mybitems.items.ErrorItem;
import com.mysaver.utils.Utils;

/**
 * Receiver that collects data related to device's audio sensor
 * 
 * @author Marcel Cunha
 * 
 */
public class AudioReceiver extends BaseReceiver {

	private AudioManager mManager;
	private String mId;
	private static final String TAG = "AudioReceiver";

	public AudioReceiver(Context context) {
		super(context);

		try {
			mId = Utils.getId(context);
			Log.d(TAG, "Creating Audio Receiver : " + mId);
			this.mManager = ((AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE));

			saveSensor();
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * Checks if music is playing
	 * 
	 * @return if music is being played
	 */
	public final boolean playingMusic() {

		boolean bool = false;
		try {
			bool = mManager.isMusicActive();
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
			bool = false;
		}
		Log.d(TAG, "playingMusic : " + bool);
		return bool;
	}

	/**
	 * Gets the music volume
	 * 
	 * @return int music volume
	 */
	public final int getMusicVolume() {
		int volume = 0;
		try {
			volume = mManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
			volume = 0;
		}
		Log.d(TAG, "musicVolume : " + volume);
		return volume;
	}

	/**
	 * Gets the call volume
	 * 
	 * @return int call volume
	 */
	public final int getCallVolume() {

		int volume = 0;
		try {
			volume = mManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
			volume = 0;
		}
		Log.d(TAG, "callVolume : " + volume);
		return volume;
	}

	/**
	 * Gets the ring volume
	 * 
	 * @return int ring volume
	 */
	public final int getRingVolume() {

		int volume = 0;
		try {
			volume = mManager.getStreamVolume(AudioManager.STREAM_RING);
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
			volume = 0;
		}
		Log.d(TAG, "ringVolume : " + volume);
		return volume;
	}

	/**
	 * Checks if the speaker is on
	 * 
	 * @return boolean indicating if the speaker is on
	 */
	public final boolean speakerOn() {

		boolean bool = false;
		try {
			bool = mManager.isSpeakerphoneOn();

		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
			bool = false;
		}

		Log.d(TAG, "speakerOn : " + bool);
		return bool;
	}

	/**
	 * Gets the audio mode
	 * 
	 * @return int indicating the audio mode
	 */
	public final int getMode() {

		int mode = -1;
		try {
			mode = mManager.getMode();
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
			mode = -1;
		}

		Log.d(TAG, "getMode : " + mode);
		return mode;

	}

	@Override
	public void saveSensor() {
		Log.d(TAG, "saveSensor");
		AudioItem item = null;
		try {
			item = new AudioItem(mId);
			item.setCallVolume(getCallVolume());
			item.setMode(getMode());
			item.setMusicOn(playingMusic());
			item.setMusicVolume(getMusicVolume());
			item.setRingVolume(getRingVolume());
			item.setSpeakerOn(speakerOn());
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}

		addItem(item);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Do nothing
	}

}
