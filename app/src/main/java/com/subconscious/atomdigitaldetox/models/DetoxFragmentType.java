package com.subconscious.atomdigitaldetox.models;

import com.subconscious.atomdigitaldetox.fragments.ContentBaseFragment;
import com.subconscious.atomdigitaldetox.fragments.DetoxSettingsFragment;
import com.subconscious.atomdigitaldetox.fragments.DetoxTimerFragment;

public enum DetoxFragmentType {
    SETTINGS,
    TIMER,
    RESULT;

    public static DetoxFragmentType getFragmentType(ContentBaseFragment contentBaseFragment) {
        if (contentBaseFragment instanceof DetoxSettingsFragment) {
            return SETTINGS;
        } else if (contentBaseFragment instanceof DetoxTimerFragment) {
            return TIMER;
        } else return RESULT;
    }
}
