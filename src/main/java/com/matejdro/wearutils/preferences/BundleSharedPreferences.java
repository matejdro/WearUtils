package com.matejdro.wearutils.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class BundleSharedPreferences implements SharedPreferences {
    private final Bundle storage;
    private final
    @Nullable
    SharedPreferences originalPreferences;

    public BundleSharedPreferences(@Nullable SharedPreferences originalValues, Bundle storage) {
        this.storage = storage;
        originalPreferences = originalValues;
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(String key, String defValue) {
        if (storage.containsKey("setting_".concat(key)))
            return storage.getString("setting_".concat(key));

        if (originalPreferences == null) {
            return defValue;
        }

        return originalPreferences.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInt(String key, int defValue) {
        if (storage.containsKey("setting_".concat(key)))
            return storage.getInt("setting_".concat(key));

        if (originalPreferences == null) {
            return defValue;
        }

        return originalPreferences.getInt(key, defValue);

    }

    @Override
    public long getLong(String key, long defValue) {
        if (storage.containsKey("setting_".concat(key)))
            return storage.getLong("setting_".concat(key));

        if (originalPreferences == null) {
            return defValue;
        }

        return originalPreferences.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (storage.containsKey("setting_".concat(key)))
            return storage.getFloat("setting_".concat(key));

        if (originalPreferences == null) {
            return defValue;
        }

        return originalPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (storage.containsKey("setting_".concat(key)))
            return storage.getBoolean("setting_".concat(key));

        if (originalPreferences == null) {
            return defValue;
        }

        return originalPreferences.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return (originalPreferences != null && originalPreferences.contains(key)) || storage.containsKey("setting_".concat(key));
    }

    @Override
    public Editor edit() {
        return new Editor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        throw new UnsupportedOperationException();
    }

    public class Editor implements SharedPreferences.Editor {
        @Override
        public SharedPreferences.Editor putString(String s, String s2) {
            storage.putString("setting_".concat(s), s2);
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String s, Set<String> strings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SharedPreferences.Editor putInt(String s, int i) {
            storage.putInt("setting_".concat(s), i);
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String s, long l) {
            storage.putLong("setting_".concat(s), l);
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String s, float v) {
            storage.putFloat("setting_".concat(s), v);
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String s, boolean b) {
            storage.putBoolean("setting_".concat(s), b);
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String s) {
            storage.remove("setting_".concat(s));
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            storage.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return true;
        }

        @Override
        public void apply() {

        }
    }

    public static void applyPreferencesFromBundle(SharedPreferences.Editor editor, Bundle data) {
        for (String key : data.keySet()) {
            if (!key.startsWith("setting_")) {
                continue;
            }

            writePreferenceIntoSharedPreferences(editor, key.substring(8), data.get(key));
        }
    }

    public static void writePreferenceIntoSharedPreferences(SharedPreferences.Editor editor, String key, Object object) {
        if (object instanceof Integer)
            editor.putInt(key, (Integer) object);
        else if (object instanceof Boolean)
            editor.putBoolean(key, (Boolean) object);
        else if (object instanceof String)
            editor.putString(key, (String) object);
        else if (object instanceof Float)
            editor.putFloat(key, (Float) object);
        else if (object instanceof Long)
            editor.putLong(key, (Long) object);
        else
            Timber.e("Unknown type for preferences: %s (key: %s)", object.getClass().getName(), key);
    }
}
