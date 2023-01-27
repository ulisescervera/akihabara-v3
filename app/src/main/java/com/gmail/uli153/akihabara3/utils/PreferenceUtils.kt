package com.gmail.uli153.akihabara3.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtils(private val context: Context) {

    enum class PreferenceKeys(val key: String) {
        FilterBoardgame("FILTER_BOARDGAMES"),
        FilterBoardgameAccessory("FILTER_BOARDGAME_ACCESSORY"),
        FilterBoardGameExpansion("FILTER_BOARDGAME_EXPANSION"),
        FilterVideogame("FILTER_VIDEOGAME"),
        FirstTimeStarted("FIRST_TIME_STARTED_APP"),
        GridMode("DISPLAY_GRID_MODE")
    }

    private val PREFERENCE_FILE = "AKB_PREFERENCE_FILE"

    private val preferences: SharedPreferences get() {
        return context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
    }

    private val editor: SharedPreferences.Editor get() {
        return preferences.edit()
    }

    fun getBoolean(key: PreferenceKeys, default: Boolean = false): Boolean {
        return preferences.getBoolean(key.key, default)
    }

    fun putBoolean(key: PreferenceKeys, value: Boolean) {
        editor.putBoolean(key.key, value).apply()
    }
}
