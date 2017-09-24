package com.matejdro.wearutils.ui;

import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.matejdro.wearutils.companionnotice.WearCompanionPhoneActivity;

/**
 * Activity that can swap fragments between both support and regular fragments.
 * This is used to allow using new compat fragments while still supporting regular
 * PreferenceFragment which has much less issues than compat ones do.
 */
public abstract class DualFragmentManagerActivity extends WearCompanionPhoneActivity {
    private Object currentFragment = null;

    public @Nullable
    Object getCurrentFragment() {
        return currentFragment;
    }

    protected void swapFragment(Fragment newFragment) {
        if (currentFragment instanceof android.app.Fragment) {
            // We need to swap between support and regular fragment manager
            getFragmentManager().beginTransaction()
                    .remove((android.app.Fragment) currentFragment)
                    .commit();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(getFragmentContainerId(), newFragment)
                .commit();

        updateCurrentFragment(newFragment);
    }

    protected void swapFragment(android.app.Fragment newFragment) {
        if (currentFragment instanceof Fragment) {
            // We need to swap between support and regular fragment manager
            getSupportFragmentManager().beginTransaction()
                    .remove((Fragment) currentFragment)
                    .commit();
        }

        getFragmentManager().beginTransaction()
                .replace(getFragmentContainerId(), newFragment)
                .commit();

        updateCurrentFragment(newFragment);
    }


    @CallSuper
    protected void updateCurrentFragment(@Nullable Object newFragment) {
        if (newFragment == null) {
            return;
        }

        currentFragment = newFragment;


    }

    protected abstract @IdRes
    int getFragmentContainerId();
}
