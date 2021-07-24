package com.subconscious.atomdigitaldetox.helper;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class UsageUtils {

    private UsageStatsManager usageStatsManager;
    private PackageManager packageManager;
    private Context context;
    private static UsageUtils usageUtils;

    private UsageUtils(Context context) {
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        packageManager = this.context.getPackageManager();
        usageStatsManager = (UsageStatsManager) this.context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public synchronized static UsageUtils getInstance(Context context) {
        if (null == usageUtils)
            usageUtils = new UsageUtils(context);
        return usageUtils;
    }

    public String getTopUsedApp(long endTime, long window, Context context, String appPackage) {
        String topPackageName = "null";
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, window, endTime);
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                if (!Utils.isApplicationInForeground(context, appPackage) && topPackageName.equals(appPackage)) {
                    mySortedMap.remove(mySortedMap.lastKey());
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }
        Log.v("Package ", topPackageName);
        return topPackageName;
    }

    public String getLauncherPackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return defaultLauncher.activityInfo.packageName;
    }

}
