package com.subconscious.atomdigitaldetox.helper;


import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

import com.subconscious.atomdigitaldetox.models.PermissionData;
import com.subconscious.atomdigitaldetox.store.PermissionsStore;

import java.util.ArrayList;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class PermissionUtil {
    private static final int USAGE_ACCESS = 1;
    private static final int NOTIFICATION_ACCESS = 2;
    private static final int BATTERY_OPTIMIZATION = 3;
    private static ArrayList<PermissionData> permissionDataArrayList;

    private static boolean isUsageAccessEnabled(Context context) {
        AppOpsManager appopmanager = null;
        int mode = 0;
        appopmanager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        mode = appopmanager.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(),
                context.getPackageName()
        );
        return mode == MODE_ALLOWED;
    }

    private static boolean isNotificationAccessEnabled(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners").contains(context.getPackageName());
    }

    private static boolean isBatteryOptimizationEnabled(Context context) {
        Intent intent = new Intent();
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            return true;
        }
        return false;
    }

    public static void requestUsageAccess(Context context) {
        context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    public static void requestNotificationAccess(Context context) {
        context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    public static void requestBatteryOptimizationRevoke(Context context) {
        Intent intent = new Intent();
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    public static void getPermission(Context context) {
        PermissionsDataConfigurer.getInstance(context);
        PermissionsStore permissionsStore = PermissionsStore.getInstance();
        permissionDataArrayList = permissionsStore.getPermissionsData();
    }

    public static boolean checkPermission(Context context) {
        int permissionGrantedCount = 0;
        int totalPermissions = 0;
        for (PermissionData permissionData : permissionDataArrayList) {
            permissionData.setPermissionGranted(isPermissionEnabled(permissionData.getId(), context));
            if (permissionData.isPermissionGranted())
                permissionGrantedCount++;
            totalPermissions++;
        }
        if (permissionGrantedCount != totalPermissions) return false;
        return true;
    }

    public static boolean isPermissionEnabled(int permissionId, Context context) {
        switch (permissionId) {
            case PermissionUtil.USAGE_ACCESS:
                return PermissionUtil.isUsageAccessEnabled(context);
            case PermissionUtil.NOTIFICATION_ACCESS:
                return PermissionUtil.isNotificationAccessEnabled(context);
            case PermissionUtil.BATTERY_OPTIMIZATION:
                return PermissionUtil.isBatteryOptimizationEnabled(context);

            default:
                break;
        }
        return false;
    }

    public static void givePermission(Context context) {
        for (PermissionData permissionData : permissionDataArrayList) {
            permissionData.setPermissionGranted(isPermissionEnabled(permissionData.getId(), context));
            if (permissionData.isPermissionGranted()) continue;
            else {
                if (permissionData.getId() == PermissionUtil.USAGE_ACCESS)
                    PermissionUtil.requestUsageAccess(context);
                else if (permissionData.getId() == PermissionUtil.NOTIFICATION_ACCESS)
                    PermissionUtil.requestNotificationAccess(context);
                else if (permissionData.getId() == PermissionUtil.BATTERY_OPTIMIZATION)
                    PermissionUtil.requestBatteryOptimizationRevoke(context);
            }
        }
    }

}
