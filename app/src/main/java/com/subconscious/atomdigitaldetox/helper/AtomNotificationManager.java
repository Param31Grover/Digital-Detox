package com.subconscious.atomdigitaldetox.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.subconscious.atomdigitaldetox.R;

public class AtomNotificationManager {

    private static final int NOTIFICATION_ID_COUNTDOWN_START = 110;
    private static final String NOTIFICATION_CHANNEL_ID_COUNTDOWN_START = "TREEPLANT";
    private static final int FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START = 111;
    private static final String FOREGROUND_NOTIFICATION_CHANNEL_ID_COUNTDOWN_START = "TIMELEFT";

    public static NotificationCompat.Builder displayCountdown(Context context, NotificationManager notificationManager) {
        notificationManager.cancel(NOTIFICATION_CHANNEL_ID_COUNTDOWN_START, NOTIFICATION_ID_COUNTDOWN_START);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_heartbeat)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_COUNTDOWN_START, "Alert", NotificationManager.IMPORTANCE_HIGH));
        }
        builder.setChannelId(NOTIFICATION_CHANNEL_ID_COUNTDOWN_START);
        notificationManager.notify(NOTIFICATION_ID_COUNTDOWN_START, builder.build());
        return builder;
    }

    public static void stopCountDown(final NotificationManager notificationManager, final int id, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId);
        }
        notificationManager.cancel(id);
    }

    public static NotificationCompat.Builder displayTimer(Context context, NotificationManager notificationManager) {
        notificationManager.cancel(FOREGROUND_NOTIFICATION_CHANNEL_ID_COUNTDOWN_START, FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_heartbeat)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(FOREGROUND_NOTIFICATION_CHANNEL_ID_COUNTDOWN_START, "Timer", NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setSound(null, null);
            notificationManager.createNotificationChannel(mChannel);
        }
        builder.setChannelId(FOREGROUND_NOTIFICATION_CHANNEL_ID_COUNTDOWN_START);
        return builder;
    }

}
