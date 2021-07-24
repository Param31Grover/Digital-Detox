package com.subconscious.atomdigitaldetox.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import lombok.Getter;

public abstract class ContentBaseFragment extends Fragment {

    @Getter
    private JSONObject result;

    public ContentBaseFragment() {
        this.result = new JSONObject();
    }

    @Nullable
    @Override
    public abstract View  onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public String getTitle()
    {
        return "";
    }

    public void onSkipPressed(){}

    public boolean onBackPressed() {
        return false;
    }
}
