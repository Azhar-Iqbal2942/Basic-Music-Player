package com.example.player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationUtil {


    public static final int NOTIFICATION_ID = 2942;
    private static final int MUSIC_NOTIFICATION_ID = 1239;
    public static final int PENDING_INTENT_ID = 5698;
    public static final String MUSIC_NOTIFICATION_CHANNEL_ID = "music_notification_channel";

    public static void musicNotification(Context context) {

        //  Get the NotificationManager using context.getSystemService
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //  Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MUSIC_NOTIFICATION_CHANNEL_ID,
                    "Music App",
                    NotificationManager.IMPORTANCE_LOW);

            nm.createNotificationChannel(mChannel);
        }
        // In this method use NotificationCompat.Builder to create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MUSIC_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle("Music Player")
                .setContentText("This is Content Text")
                .setLargeIcon(largeIcon(context))
                .addAction(R.drawable.dislike, "Dislike", null)
                .addAction(R.drawable.previous, "Previous", null)
                .addAction(R.drawable.pause, "pause", null)
                .addAction(R.drawable.next, "Next", nextSongIntent(context))
                .addAction(R.drawable.like, "Like", null)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3))
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        //If the build version is greater than or equal to JELLY_BEAN and less than OREO,
        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
        }

        // Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        nm.notify(MUSIC_NOTIFICATION_ID, builder.build());

    }

    private static PendingIntent nextSongIntent(Context context) {

        Intent nextSongIntent = new Intent(context, MusicService.class);
        nextSongIntent.setAction(Constants.ACTION_NEXT_SONG);

        PendingIntent nextSongAction = PendingIntent.getBroadcast(context,
                Constants.ACTION_NEXT_SONG_ID,
                nextSongIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return nextSongAction;
    }


    /**
     * Create a helper method called contentIntent with a single parameter for a Context. It
     * should return a PendingIntent. This method will create the pending intent which will trigger when
     * the notification is pressed. This pending intent should open up the MainActivity.
     */
    private static PendingIntent contentIntent(Context context) {

        Intent startActivityIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    //Create a helper method called largeIcon which takes in a Context as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context) {

        //  Get a Resources object from the context.
        Resources res = context.getResources();

        // Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_local_drink_black_24px
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.songcover);
        return largeIcon;
    }
}
