package com.subconscious.atomdigitaldetox;

import com.subconscious.atomdigitaldetox.helper.PermissionsDataConfigurer;
import com.subconscious.atomdigitaldetox.listeners.ActivityLifecycleCallback;

public class DetoxApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
        //Static data initiation
        PermissionsDataConfigurer.getInstance(this);
    }
}
