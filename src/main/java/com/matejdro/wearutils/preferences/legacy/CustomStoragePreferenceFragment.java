package com.matejdro.wearutils.preferences.legacy;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.matejdro.wearutils.preferences.PreferenceSource;

import java.lang.reflect.Field;

public abstract class CustomStoragePreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof PreferenceSource) {
            SharedPreferences sharedPreferences = ((PreferenceSource) getActivity()).getCustomPreferences();
            injectSharedPreferences(sharedPreferences);
        }
    }

    @SuppressLint("CommitPrefEdits")
    protected void injectSharedPreferences(SharedPreferences preferences) {
        PreferenceManager manager = getPreferenceManager();

        try {
            Field field = PreferenceManager.class.getDeclaredField("mSharedPreferences");
            field.setAccessible(true);
            field.set(manager, preferences);

            field = PreferenceManager.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            field.set(manager, preferences.edit());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
