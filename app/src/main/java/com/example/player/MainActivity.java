package com.example.player;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Song> songList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SongAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int MY_PERMISSION_REQUEST = 1;
    public Intent playIntent;
    public boolean mBound;
    public boolean mPlay = false;
    public boolean mNewConnection = true;
    MusicService mService;
    FloatingActionButton fab;
    StorageUtil util;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        PermissionMethod();
        getMusic();

        playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

        displayListItem();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNewConnection && !mPlay) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
                    mService.resumeTrack();
                    mPlay = true;
                    Log.i("MainActivity", "Song is Playing");


                } else if (mPlay) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
                    mService.pauseSound();
                    mPlay = false;

                } else if (!mPlay) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
                    mService.resumeSound();
                    mPlay = true;

                }
            }
        });


    }

    // CONNECTING TO THE SERVICE CLASS
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // this will make the connection to the service class
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            // this will take the service from service class
            mService = binder.getService();
            // pass the list of songs
            mService.setList(songList);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeData();
        unbindService(musicConnection);
    }

    public void storeData() {
        util.storeAudioPosition(mService.resumePosition);
        util.storeAudioIndex(mService.songIndex);
        Log.i("MainActivity", " Data Stored ");
    }


    public void PermissionMethod() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);

            }
        }

    }

    public void displayListItem() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SongAdapter(songList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPlay = true;
                mNewConnection = false;
                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
                mService.playSong(position);

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(this, "No Permission Granteed!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public void getMusic() { // These are content provider operation


        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null,
                null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                long currentId = songCursor.getLong(songID);


                songList.add(new Song(currentTitle, currentArtist, currentLocation, currentId));
            } while (songCursor.moveToNext());
        }

    }


    public class LoadSong extends AsyncTask<Void, Void, ArrayList<Song>> {

        @Override
        protected ArrayList<Song> doInBackground(Void... voids) {
            ArrayList<Song> songs = new ArrayList<>();

            ContentResolver contentResolver = getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor songCursor = contentResolver.query(songUri, null, null,
                    null, null);
            if (songCursor != null && songCursor.moveToFirst()) {
                int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);

                do {
                    String currentTitle = songCursor.getString(songTitle);
                    String currentArtist = songCursor.getString(songArtist);
                    String currentLocation = songCursor.getString(songLocation);
                    long currentId = songCursor.getLong(songID);


                    songs.add(new Song(currentTitle, currentArtist, currentLocation, currentId));
                } while (songCursor.moveToNext());
            }
            return songs;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> arrayList) {
            super.onPostExecute(arrayList);
            songList = arrayList;
        }
    }
}
