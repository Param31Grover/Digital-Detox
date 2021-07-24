package com.subconscious.atomdigitaldetox.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

import com.subconscious.atomdigitaldetox.services.DigitalDetoxService;

public class ServiceUtils {

    private Context context;
    private static ServiceUtils serviceUtils;

    private ServiceUtils(Context context) {
        this.context = context;
    }

    public synchronized static ServiceUtils getInstance(Context context) {
        if (null == serviceUtils) {
            serviceUtils = new ServiceUtils(context);
        }
        return serviceUtils;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    public void startTrackingService(Class<DigitalDetoxService> serviceClass) {
        Intent startServiceIntent = new Intent(context, serviceClass);
        context.startService(startServiceIntent);
    }

    public void stopTrackingService(Class<DigitalDetoxService> serviceClass) {
        Intent startServiceIntent = new Intent(context, serviceClass);
        context.stopService(startServiceIntent);
    }
}
