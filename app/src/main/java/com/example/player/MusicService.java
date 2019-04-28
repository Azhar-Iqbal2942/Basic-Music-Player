package com.example.player;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service
        implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {


    /***
     *Global Variable Declaration..
     *
     */

    public static final String TAG = "Service Class ";

    private ArrayList<Song> song = new ArrayList<>();
    public MediaPlayer mediaPlayer;
    public int songIndex;
    public boolean mNewConnection;
    public int resumePosition = 0;  // this will contain current song position


    private final IBinder musicBinder = new MusicBinder();

    public void onCreate() {
        super.onCreate();
        Log.i("Music Service", " Service Connected");
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }


    /***
     * Public Methods
     *
     */


    public void setList(ArrayList<Song> song) {
        this.song = song;
    }

    public void initMusicPlayer() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

    }

    /**
     * Playback Controls...........................
     */

    public void resumeTrack() {
        mNewConnection = true;
        mediaPlayer.reset();
        songIndex = new StorageUtil(getBaseContext()).loadAudioIndex();
        resumePosition = new StorageUtil(getBaseContext()).loadAudioPosition();
        Song playSong = song.get(songIndex);
        long currentSong = playSong.getId();

        // set URI for that Song
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mediaPlayer.setDataSource(getBaseContext(), trackUri);
        } catch (Exception e) {
            Log.e("Music Service", "Error Setting data Source");
        }

        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.seekTo(resumePosition);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        NotificationUtil.musicNotification(this);

    }


    public void playSong(int index) {
        mNewConnection = false;
        mediaPlayer.reset();
        songIndex = index;
        Song playSong = song.get(index);
        long currentSong = playSong.getId();
        // set URI for that Song
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("Music Service", "Error Setting data Source");
        }
        mediaPlayer.prepareAsync();


    }

    public void pauseSound() {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            new StorageUtil(getBaseContext()).storeAudioIndex(songIndex);
            new StorageUtil(getBaseContext()).storeAudioPosition(resumePosition);

            // Debugging stuff
            String strIndex = String.valueOf(songIndex);
            String strPos = String.valueOf(resumePosition);
            Log.i("Music Service Class", strIndex);
            Log.i("Music Service Class", strPos);

        }
    }

    public void resumeSound() {
        if (mediaPlayer.isPlaying()) return;

        mediaPlayer.start();
        mediaPlayer.seekTo(resumePosition);
        Log.i("Music Service Class", "Song Resumed");
    }

    public void nextTrack() {
        if (songIndex > song.size()) {
            stopSound();
        } else {
            songIndex++;
            playSong(songIndex);

        }
    }

    public void previousTrack() {
        if (songIndex < 0) {
            stopSound();
        } else {
            songIndex--;
            playSong(songIndex);
        }
    }


    public void stopSound() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextTrack();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mNewConnection) {
            mediaPlayer.start();
            mediaPlayer.seekTo(resumePosition);
        } else {
            mediaPlayer.start();
            //             NotificationUtil.musicNotification(this);
        }


    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }


}