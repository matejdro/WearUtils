@file:JvmName("DatabaseUtils")

package com.matejdro.wearutils.miscutils

import android.database.Cursor

inline fun <T> Cursor.toList(creator: (Cursor) -> T): List<T> {
    val list = ArrayList<T>()

    while (moveToNext()) {
        list.add(creator.invoke(this))
    }

    return list
}