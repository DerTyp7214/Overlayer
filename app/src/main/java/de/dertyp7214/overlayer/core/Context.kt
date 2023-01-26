package de.dertyp7214.overlayer.core

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

val Context.sharedPreferences: SharedPreferences
    get() = getSharedPreferences("overlayer", Context.MODE_PRIVATE)

fun <E> Context.save(key: String, value: E) {
    sharedPreferences.edit {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <E> Context.load(key: String, default: E): E {
    return when (default) {
        is String -> sharedPreferences.getString(key, default)!! as E
        is Int -> sharedPreferences.getInt(key, default) as E
        is Boolean -> sharedPreferences.getBoolean(key, default) as E
        is Float -> sharedPreferences.getFloat(key, default) as E
        is Long -> sharedPreferences.getLong(key, default) as E
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

fun Context.remove(key: String) = sharedPreferences.edit { remove(key) }

fun Context.getDimen(id: Int) = resources.getDimension(id)

fun Context.getAttrColor(id: Int): Int {
    val typedValue = android.util.TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}