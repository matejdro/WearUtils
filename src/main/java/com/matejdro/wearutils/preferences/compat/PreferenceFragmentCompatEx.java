package com.matejdro.wearutils.preferences.compat;

import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

/**
 * PreferenceFragmentCompat that adds support for custom preferences with dialogs
 */
public abstract class PreferenceFragmentCompatEx extends PreferenceFragmentCompat {
    public static final String DIALOG_FRAGMENT_TAG =
            "android.support.v7.preference.PreferenceFragment.DIALOG";

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // check if dialog is already showing
        //noinspection ConstantConditions
        if (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        if (preference instanceof PreferenceWithDialog) {
            PreferenceDialogFragmentCompat dialog =
                    ((PreferenceWithDialog) preference).createDialog(preference.getKey());

            dialog.setTargetFragment(this, 0);
            dialog.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            return;
        }

        super.onDisplayPreferenceDialog(preference);
    }
}
