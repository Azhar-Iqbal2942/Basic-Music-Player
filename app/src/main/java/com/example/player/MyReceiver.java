package com.example.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

//    public static final String ACTION_PAUSE = "com.example.player.pause";
//    public static final String ACTION_PLAY = "com.example.player.play";
//    public static final String ACTION_PREV = "com.example.player.previous";
//    public static final String ACTION_NEXT = "com.example.player.next";

    private MusicService mService;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals(Constants.ACTION_NEXT_SONG)) {
            mService.nextTrack();
            Toast.makeText(context, "Song Update", Toast.LENGTH_SHORT).show();
        }


    }
}
