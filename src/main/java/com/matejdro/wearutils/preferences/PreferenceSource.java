package com.matejdro.wearutils.preferences;

import android.content.SharedPreferences;

/**
 * @deprecated Use {@link androidx.preference.PreferenceDataStore}-based interfaces instead
 */
@Deprecated
public interface PreferenceSource
{
    SharedPreferences getCustomPreferences();
}
