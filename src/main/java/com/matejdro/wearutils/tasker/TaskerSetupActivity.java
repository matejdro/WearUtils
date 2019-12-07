package com.matejdro.wearutils.tasker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class TaskerSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!loadIntent()) {
            onFreshTaskerSetup();
        }

        super.onCreate(savedInstanceState);
    }

    private boolean loadIntent() {
        Intent intent = getIntent();

        if (intent == null)
            return false;

        Bundle bundle = intent.getBundleExtra(LocaleConstants.EXTRA_BUNDLE);
        if (bundle == null)
            return false;

        return onPreviousTaskerOptionsLoaded(bundle);
    }

    protected abstract boolean onPreviousTaskerOptionsLoaded(Bundle taskerOptions);
    protected void onFreshTaskerSetup() {

    }
}
