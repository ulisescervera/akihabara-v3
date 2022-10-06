package com.gmail.uli153.akihabara3.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtils(private val context: Context) {

    private val PREFERENCE_FILE = "AKB_PREFERENCE_FILE"

    val preferences: SharedPreferences get() {
        return context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
    }

    private val editor: SharedPreferences.Editor get() {
        return preferences.edit()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return preferences.getBoolean(key, default)
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }
}
