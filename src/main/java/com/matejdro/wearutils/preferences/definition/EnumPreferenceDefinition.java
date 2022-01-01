package com.matejdro.wearutils.preferences.definition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumPreferenceDefinition<T extends Enum<T>> implements PreferenceDefinition<T> {
    private final String key;
    private final T defaultValue;
    private final Method valueOfMethod;

    public EnumPreferenceDefinition(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;

        try {
            valueOfMethod = defaultValue.getDeclaringClass().getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Enum does not have valueOf() method!");
        }

    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public T getEnumValueFromString(String name)
    {
        try {
            return (T) valueOfMethod.invoke(null, name);
        } catch (IllegalAccessException e) {
            return defaultValue;
        } catch (InvocationTargetException e) {
            return defaultValue;
        }
    }
}
