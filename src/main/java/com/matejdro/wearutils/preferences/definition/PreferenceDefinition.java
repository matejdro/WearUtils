package com.matejdro.wearutils.preferences.definition;

public interface PreferenceDefinition<T> {
    String getKey();
    T getDefaultValue();
}
