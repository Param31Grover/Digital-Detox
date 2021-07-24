package com.subconscious.atomdigitaldetox.store;

import com.subconscious.atomdigitaldetox.helper.PermissionsDataConfigurer;
import com.subconscious.atomdigitaldetox.models.PermissionData;

import java.util.ArrayList;

public class PermissionsStore {
    private static PermissionsStore instance = null;


    private ArrayList<PermissionData> permissionsData;

    public static synchronized PermissionsStore getInstance() {
        if (instance == null) {
            instance = new PermissionsStore();
        }

        return instance;
    }

    private PermissionsStore() {
        init();
    }

    private void init()
    {
        permissionsData = new ArrayList<>();
        permissionsData.addAll(PermissionsDataConfigurer.getPermissionDataArrayList());
    }

    public ArrayList<PermissionData> getPermissionsData() {
        return permissionsData;
    }
}
