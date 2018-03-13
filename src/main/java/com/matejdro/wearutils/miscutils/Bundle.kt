@file:JvmName("BundleUtils")

package com.matejdro.wearutils.miscutils

import android.os.Bundle

inline fun <reified T : Enum<T>> Bundle.getEnum(key : String) : T? {
    val name = getString(key) ?: return null

    try {
        val valueOfMethod = T::class.java.getMethod("valueOf", String::class.java)
        return valueOfMethod.invoke(null, name) as T
    } catch (e: NoSuchMethodException) {
        throw IllegalArgumentException("Enum does not have valueOf() method!")
    }

}

fun <T : Enum<T>> Bundle.putEnum(key : String, value : T){
    putString(key, value.name)
}