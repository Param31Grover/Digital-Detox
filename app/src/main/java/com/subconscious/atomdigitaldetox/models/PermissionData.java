package com.subconscious.atomdigitaldetox.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionData {
    private int id;
    private String name;
    private String description;
    private String icon;
    private boolean isPermissionGranted;

}


