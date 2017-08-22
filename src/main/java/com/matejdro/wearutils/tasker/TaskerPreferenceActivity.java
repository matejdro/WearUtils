package com.matejdro.wearutils.tasker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.matejdro.wearutils.preferences.BundleSharedPreferences;
import com.matejdro.wearutils.preferences.PreferenceSource;

public abstract class TaskerPreferenceActivity extends TaskerSetupActivity implements PreferenceSource {
    private Bundle preferenceStorage;
    private SharedPreferences preferences;

    @Override
    protected boolean onPreviousTaskerOptionsLoaded(Bundle taskerOptions) {
        preferenceStorage = taskerOptions;
        initPreferences();
        return true;
    }

    @Override
    protected void onFreshTaskerSetup() {
        preferenceStorage = new Bundle();
        initPreferences();
    }

    protected void initPreferences() {
        preferences = new BundleSharedPreferences(getOriginalValues(), preferenceStorage);
    }

    public Bundle getPreferenceStorage() {
        return preferenceStorage;
    }

    @Override
    public SharedPreferences getCustomPreferences() {
        return preferences;
    }

    protected void save() {
        Intent intent = new Intent();

        String description = getDescription();

        intent.putExtra(LocaleConstants.EXTRA_STRING_BLURB, description);

        onPreSave(preferenceStorage, intent);
        intent.putExtra(LocaleConstants.EXTRA_BUNDLE, preferenceStorage);

        setResult(RESULT_OK, intent);
    }

    @SuppressWarnings("EmptyMethod")
    protected void onPreSave(Bundle settingsBundle, Intent taskerIntent) {

    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    @NonNull
    protected abstract String getDescription();

    @Nullable
    protected SharedPreferences getOriginalValues() {
        return null;
    }
}
