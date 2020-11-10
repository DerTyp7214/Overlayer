package de.dertyp7214.overlayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            AppCompatDelegate.setDefaultNightMode(
                getInt(
                    "theme_pref",
                    AppCompatDelegate.getDefaultNightMode()
                )
            )
        }
    }
}