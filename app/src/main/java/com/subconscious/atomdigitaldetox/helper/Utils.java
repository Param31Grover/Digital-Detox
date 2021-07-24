package com.subconscious.atomdigitaldetox.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.subconscious.atomdigitaldetox.listeners.ActivityLifecycleCallback;

public class Utils {

    public static Drawable getDrawable(Context context, String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return context.getResources().getDrawable(resourceId);
    }

    public static int addDelay(int currentLength, int delay) {
        return currentLength + delay * 1000;
    }

    public static String getDisplayString(long millis) {
        String finalTimerString = " ";
        String minutesString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((millis % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Add hours if there
        if (minutes > 0) {
            minutesString = minutes + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (hours == 0
                &&
                minutes == 0) {
            secondsString = secondsString + "s";
        }
        finalTimerString = finalTimerString + minutesString + secondsString;

        return finalTimerString;
    }

    public static boolean isApplicationInForeground(Context context, String appName) {
        return ActivityLifecycleCallback.isApplicationInForeground();
    }

}
