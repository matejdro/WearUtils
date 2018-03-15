package com.matejdro.wearutils.preferences

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceProperty<T>(private val preferences: SharedPreferences,
                            private val outputType: Class<T>) : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        val key = property.name

        if (!preferences.contains(key)) {
            return null
        }

        @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
        return when (outputType) {
            Boolean::class.java, java.lang.Boolean::class.java -> preferences.getBoolean(key, false)
            Float::class.java, java.lang.Float::class.java -> preferences.getFloat(key, 0f)
            Int::class.java, java.lang.Integer::class.java -> preferences.getInt(key, 0)
            Long::class.java, java.lang.Long::class.java -> preferences.getLong(key, 0L)
            String::class.java -> preferences.getString(key, null)
            Set::class.java -> preferences.getStringSet(key, null)
            else -> throw IllegalArgumentException("SharedPreferences do not support type ${outputType.name}")
        } as T?
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        val editor = preferences.edit()
        val key = property.name

        try {
            if (value == null) {
                editor.remove(key)
            } else {
                @Suppress("UNCHECKED_CAST")
                when (outputType) {
                    Boolean::class.java, java.lang.Boolean::class.java -> editor.putBoolean(key, value as Boolean)
                    Float::class.java, java.lang.Float::class.java -> editor.putFloat(key, value as Float)
                    Int::class.java, java.lang.Integer::class.java -> editor.putInt(key, value as Int)
                    Long::class.java, java.lang.Long::class.java -> editor.putLong(key, value as Long)
                    String::class.java -> editor.putString(key, value as String)
                    Set::class.java -> editor.putStringSet(key, value as Set<String>)
                    else -> throw IllegalArgumentException("SharedPreferences do not support type ${outputType.name}")
                }
            }
        } finally {
            editor.apply()
        }
    }
}

inline fun <reified T> SharedPreferences.property(): PreferenceProperty<T> {
    return PreferenceProperty(this, T::class.java)
}