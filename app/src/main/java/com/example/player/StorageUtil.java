package com.example.player;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class StorageUtil {

    SharedPreferences preferences;
    Context context;
    public static final String TAG = "ERROR IN STORAGE";


    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(Constants.KEY_AUDIO_INDEX, index);
        editor.apply();
        Log.d(TAG, "storeAudioIndex: Error in storing data Index ");

    }

    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_AUDIO_INDEX, 2);
    }


    public void storeAudioPosition(int position) {
        preferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(Constants.KEY_AUDIO_POSITION, position);
        editor.apply();

    }

    public int loadAudioPosition() {
        preferences = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_AUDIO_POSITION, 0);
    }

}
