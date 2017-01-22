package com.matejdro.wearutils.preferences;

import android.content.SharedPreferences;

public class PreferencesUtil
{
	public static int getIntFromStringPreference(SharedPreferences preferences, String key, int defValue)
	{
		String string = preferences.getString(key, null);
		if (string == null) {
			return defValue;
		}

		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException ignored) {
			return defValue;
		}
	}
}
