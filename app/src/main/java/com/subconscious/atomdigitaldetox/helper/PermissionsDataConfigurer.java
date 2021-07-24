package com.subconscious.atomdigitaldetox.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subconscious.atomdigitaldetox.R;
import com.subconscious.atomdigitaldetox.models.PermissionData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PermissionsDataConfigurer {


    private static ArrayList<PermissionData> permissionDataArrayList;
    private static PermissionsDataConfigurer permissionsDataConfigurer;

    private static Context mContext;

    public static synchronized PermissionsDataConfigurer getInstance(Context context) {
        if (null == permissionsDataConfigurer)
        {
            mContext = context;
            permissionsDataConfigurer = new PermissionsDataConfigurer();

        }
        return permissionsDataConfigurer;
    }

    private PermissionsDataConfigurer()
    {
        init();
    }

    public static Collection<? extends PermissionData> getPermissionDataArrayList() {
        return permissionDataArrayList;
    }

    private void init()
    {
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.permissiondata);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();
            Type type = new TypeToken<List<PermissionData>>() {}.getType();
            permissionDataArrayList = gson.fromJson(bufferedReader, type);
        }catch (Exception e)
        {
            Log.v("Exception", "exception");
        }
    }
}
