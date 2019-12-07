package com.matejdro.wearutils.preferences.compat;

import androidx.preference.PreferenceDialogFragmentCompat;

public interface PreferenceWithDialog {
    PreferenceDialogFragmentCompat createDialog(String key);
}
