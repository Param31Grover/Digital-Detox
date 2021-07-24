package com.subconscious.atomdigitaldetox.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.subconscious.atomdigitaldetox.DigitalDetoxActivity;
import com.subconscious.atomdigitaldetox.R;
import com.subconscious.atomdigitaldetox.helper.AtomNotificationManager;
import com.subconscious.atomdigitaldetox.helper.UsageUtils;
import com.subconscious.atomdigitaldetox.helper.Utils;
import com.subconscious.atomdigitaldetox.models.DetoxFragmentType;
import com.subconscious.atomdigitaldetox.store.SharedPrefManager;

public class DigitalDetoxService extends Service {

    /* Keys */
    private static final int NOTIFICATION_ID_COUNTDOWN_START = 110;
    private static final String NOTIFICATION_CHANNEL_ID_COUNTDOWN_START = "TREEPLANT";
    private static final int FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START = 111;
    private static final String FOREGROUND_NOTIFICATION_CHANNEL_ID_COUNTDOWN_START = "TIMELEFT";
    private static final String TREE_DURATION_LEFT_KEY = "TREEKEY";
    private static final String TOTAL_TREE_DURATION_KEY = "DURATION";
    private static final String RUNNING_KEY = "RUNNING";
    private static final String FRAGMENT_TYPE = "FRAGMENTTYPE";
    private static final int COUNTDOWN_DURATION = 11000;
    private static final int VIBRATION_DURATION = 800;
    private static final int INTERVAL_DURATION = 1000;
    private static final int DELAY_DURATION = 100;
    private long TREE_DURATION;

    /* Variables */
    private Boolean inFocus;
    private Boolean vibrate = true;
    private Context context;
    private Handler handler;
    private Runnable run;
    /* View Holders*/
    private CountDownTimer countDownTimer;
    private CountDownTimer treeTimer;

    /*Notification Holders */
    private NotificationCompat.Builder countDownBuilder;
    private NotificationCompat.Builder treeBuilder;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /* Service -- START -- */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        initTreeNotification();
        startTreeTimer();
        return START_STICKY;
    }

    private void init() {
        setupHandler();
        context = getApplicationContext();
        handler.postDelayed(run, 1000);
        inFocus = true;
        TREE_DURATION = SharedPrefManager.getLong(context, TREE_DURATION_LEFT_KEY);
        if (countDownTimer != null) countDownTimer.cancel();
        if (treeTimer != null) treeTimer.cancel();
    }

    private void setupHandler() {
        handler = new Handler();
        run = () -> {
            checkForAnotherPackage();
            if (inFocus) handler.postDelayed(run, 1000);

        };
    }

    /* Initialize Notification --START--*/
    public void initTreeNotification() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        treeBuilder = AtomNotificationManager.displayTimer(context, notificationManager);
        treeBuilder.setContentIntent(getPendingIntent(DetoxFragmentType.TIMER));
    }

    private void initCountDownNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        countDownBuilder = AtomNotificationManager.displayCountdown(this, notificationManager);
        startForeground(NOTIFICATION_ID_COUNTDOWN_START, countDownBuilder.build());
    }
    /* Initialize Notification --END--*/

    private void checkForAnotherPackage() {
        if (isAnotherApplicationRunning()) {
            initCountDownNotification();
            startCountdown();
            inFocus = false;
        }
    }

    private void startTreeTimer() {
        if (treeTimer != null) treeTimer.cancel();
        if (!SharedPrefManager.getBoolean(context, RUNNING_KEY)) return;
        treeTimer = new CountDownTimer(TREE_DURATION, INTERVAL_DURATION) {
            @Override
            public void onTick(long l) {
                TREE_DURATION -= 1000;
                l = TREE_DURATION;
                SharedPrefManager.setLong(context, TREE_DURATION_LEFT_KEY, l);
                if (!SharedPrefManager.getBoolean(context, RUNNING_KEY)) {
                    AtomNotificationManager.stopCountDown(notificationManager, FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START, FOREGROUND_NOTIFICATION_CHANNEL_ID_COUNTDOWN_START);
                    initTreeNotification();
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    treeBuilder.setContentIntent(getPendingIntent(DetoxFragmentType.TIMER));
                    v.vibrate(800);
                    updateNotification(notificationManager, treeBuilder,
                            FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START,
                            getString(R.string.detox_failed), getString(R.string.dead_tree), false);
                    treeTimer.cancel();
                    onDestroy();
                    return;
                }
                updateNotification(notificationManager, treeBuilder,
                        FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START,
                        getString(R.string.growing_tree),
                        getString(R.string.seconds_remaining) + Utils.getDisplayString(l),
                        true);
            }

            @Override
            public void onFinish() {
                AtomNotificationManager.stopCountDown(notificationManager, NOTIFICATION_ID_COUNTDOWN_START, NOTIFICATION_CHANNEL_ID_COUNTDOWN_START);
                initTreeNotification();
                treeBuilder.setContentIntent(getPendingIntent(DetoxFragmentType.RESULT));
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(800);
                updateNotification(notificationManager, treeBuilder,
                        FOREGROUND_NOTIFICATION_ID_COUNTDOWN_START,
                        getString(R.string.detox_complete),
                        getString(R.string.planted_tree),
                        false);
                onDestroy();
            }
        }.start();
    }

    private void updateNotification(NotificationManager notificationManager, NotificationCompat.Builder builder,
                                    int channelID, String contentTile, String contentText, boolean isOngoing) {
        builder.setContentTitle(contentTile);
        builder.setContentText(contentText);
        builder.setOngoing(isOngoing);
        notificationManager.notify(channelID, builder.build());
    }

    /* PackageTrack CountDown --START-- */
    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_DURATION, INTERVAL_DURATION) {
            public void onTick(long millisUntilFinished) {
                setTimer(millisUntilFinished);
                if (!isAnotherApplicationRunning()) {
                    onFocusRegained();
                    countDownTimer.cancel();
                    return;
                }
                notificationManager.notify(NOTIFICATION_ID_COUNTDOWN_START, countDownBuilder.build());
            }

            public void onFinish() {
                notificationManager.cancel(NOTIFICATION_CHANNEL_ID_COUNTDOWN_START, NOTIFICATION_ID_COUNTDOWN_START);
                countDownBuilder.setContentTitle(getString(R.string.click_to_go_to_app));
                countDownBuilder.setContentText(getString(R.string.dead_tree));
                countDownBuilder.setContentIntent(getPendingIntent(DetoxFragmentType.TIMER));
                SharedPrefManager.setBoolean(context, RUNNING_KEY, false);
                countDownTimer.cancel();
                onDestroy();
            }
        }.start();
    }
    /* PackageTrack CountDown --END-- */

    /* Timer Handler --Start-- */
    private void setTimer(long millisUntilFinished) {
        countDownBuilder.setContentTitle(getString(R.string.click_to_go_to_app));
        countDownBuilder.setContentText(getString(R.string.seconds_remaining) + " " + millisUntilFinished / 1000);
        if (vibrate) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(VIBRATION_DURATION);
            vibrate = false;
        } else vibrate = true;
        countDownBuilder.setContentIntent(getPendingIntent(DetoxFragmentType.TIMER));
    }

    private void onFocusRegained() {
        handler.postDelayed(run, DELAY_DURATION);
        inFocus = true;
        notificationManager.cancel(NOTIFICATION_ID_COUNTDOWN_START);
        AtomNotificationManager.stopCountDown(notificationManager, NOTIFICATION_ID_COUNTDOWN_START, NOTIFICATION_CHANNEL_ID_COUNTDOWN_START);
    }
    /* Timer Handler --END-- */

    /* Helper Method --START-- */
    private PendingIntent getPendingIntent(DetoxFragmentType fragmentType) {
        Intent resultIntent = new Intent(getApplicationContext(), DigitalDetoxActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.putExtra(FRAGMENT_TYPE, fragmentType);
        resultIntent.putExtra("test", "test");
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private boolean isAnotherApplicationRunning() {
        String applicationPackageName = getPackageName();
        String topPackageName = UsageUtils.getInstance(context).getTopUsedApp(System.currentTimeMillis(), 1000, context, getPackageName());
        String defaultLauncherStr = UsageUtils.getInstance(context).getLauncherPackage();
        if (!topPackageName.equals(defaultLauncherStr) && !topPackageName.equals(applicationPackageName) && !topPackageName.equals("null")) {
            return true;
        }
        return false;
    }
    /* Helper Method -- END -- */

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(run);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
