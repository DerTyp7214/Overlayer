package de.dertyp7214.overlayer.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.components.BaseActivity
import de.dertyp7214.overlayer.components.BottomSheetPreference
import de.dertyp7214.overlayer.screens.SettingsScreen

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

        preferenceManager.findPreference<BottomSheetPreference>("app_theme")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                AppCompatDelegate.setDefaultNightMode((newValue as String).toInt())
                sharedPreferences.edit { putInt("theme_pref", newValue.toInt()) }
                true
            }
        }

        preferenceManager.findPreference<SwitchPreference>("amoled_theme")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    sharedPreferences.edit { putString("theme", BaseActivity.AMOLED) }
                } else {
                    sharedPreferences.edit { putString("theme", BaseActivity.DEFAULT) }
                }
                activity?.apply {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, SettingsScreen::class.java))
                        finish()
                    }, resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                }
                true
            }
        }
    }
}