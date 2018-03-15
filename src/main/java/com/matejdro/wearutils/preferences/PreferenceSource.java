package com.matejdro.wearutils.preferences;

import android.content.SharedPreferences;

/**
 * @deprecated Use {@link android.support.v7.preference.PreferenceDataStore}-based interfaces instead
 */
@Deprecated
public interface PreferenceSource
{
    SharedPreferences getCustomPreferences();
}
