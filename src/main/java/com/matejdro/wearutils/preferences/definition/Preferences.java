package com.matejdro.wearutils.preferences.definition;

import android.content.SharedPreferences;
import android.net.Uri;

import com.matejdro.wearutils.miscutils.ArrayUtils;
import com.matejdro.wearutils.preferences.PreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Preferences {
    public static int getInt(SharedPreferences preferences, PreferenceDefinition<Integer> preference)
    {
        return PreferencesUtil.getIntFromStringPreference(preferences, preference.getKey(), preference.getDefaultValue());
    }

    public static boolean getBoolean(SharedPreferences preferences, PreferenceDefinition<Boolean> preference)
    {
        return preferences.getBoolean(preference.getKey(), preference.getDefaultValue());
    }

    public static String getString(SharedPreferences preferences, PreferenceDefinition<String> preference)
    {
        return preferences.getString(preference.getKey(), preference.getDefaultValue());
    }

    public static Uri getUri(SharedPreferences preferences, PreferenceDefinition<Uri> preference)
    {
        String uriString = preferences.getString(preference.getKey(), null);
        if (uriString == null) {
            return preference.getDefaultValue();
        }

        return Uri.parse(uriString);
    }


    public static long[] getLongArray(SharedPreferences preferences, PreferenceDefinition<long[]> preference)
    {
        String listString = preferences.getString(preference.getKey(), null);
        if (listString == null) {
            return preference.getDefaultValue();
        }

        long[] out = ArrayUtils.parseLongArray(listString);
        if (out == null) {
            return preference.getDefaultValue();
        }

        return out;
    }

    public static List<String> getStringList(SharedPreferences preferences, PreferenceDefinition<List<String>> preference)
    {
        String serializedString = preferences.getString(preference.getKey(), null);
        if (serializedString == null) {
            return preference.getDefaultValue();
        }

        try {
            JSONArray jsonArray = new JSONArray(serializedString);
            List<String> list = new ArrayList<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }

            return list;
        } catch (JSONException e) {
            return preference.getDefaultValue();
        }
    }

    public static <T extends Enum<T>> T getEnum(SharedPreferences preferences, EnumPreferenceDefinition<T> preference)
    {
        return preference.getEnumValueFromString(preferences.getString(preference.getKey(), null));
    }

    public static void putInt(SharedPreferences.Editor editor, PreferenceDefinition<Integer> preference, int value)
    {
        editor.putString(preference.getKey(), Integer.toString(value));
    }

    public static void putString(SharedPreferences.Editor editor, PreferenceDefinition<String> preference, String value)
    {
        editor.putString(preference.getKey(), value);
    }

    public static void putBoolean(SharedPreferences.Editor editor, PreferenceDefinition<Boolean> preference, Boolean value)
    {
        editor.putBoolean(preference.getKey(), value);
    }

    public static <T extends Enum<T>> void putEnum(SharedPreferences.Editor editor, PreferenceDefinition<T> preference, T value)
    {
        editor.putString(preference.getKey(), value.name());
    }

    public static void putLongArray(SharedPreferences.Editor editor, PreferenceDefinition<long[]> preference, long[] value)
    {
        putLongArray(editor, preference.getKey(), value);
    }

    public static void putLongArray(SharedPreferences.Editor editor, String key, long[] value)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.length; i++)
        {
            if (i != 0) {
                builder.append(',');
            }

            builder.append(value[i]);
        }

        editor.putString(key, builder.toString());
    }

}
