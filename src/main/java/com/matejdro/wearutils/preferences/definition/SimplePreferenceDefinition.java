package com.matejdro.wearutils.preferences.definition;

public class SimplePreferenceDefinition<T> implements PreferenceDefinition<T> {
    private final String key;
    private final T defaultValue;

    public SimplePreferenceDefinition(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
}
