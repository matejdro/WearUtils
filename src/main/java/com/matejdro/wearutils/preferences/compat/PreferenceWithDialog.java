package com.matejdro.wearutils.preferences.compat;

import android.support.v7.preference.PreferenceDialogFragmentCompat;

public interface PreferenceWithDialog {
    PreferenceDialogFragmentCompat createDialog(String key);
}
