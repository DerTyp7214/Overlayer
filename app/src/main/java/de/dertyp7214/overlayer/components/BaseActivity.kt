package de.dertyp7214.overlayer.components

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.dertyp7214.overlayer.R

open class BaseActivity : AppCompatActivity() {

    lateinit var currentTheme: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
            getTheme(
                PreferenceManager.getDefaultSharedPreferences(this).getString("theme", DEFAULT)!!
                    .apply { currentTheme = this }
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (currentTheme != PreferenceManager.getDefaultSharedPreferences(this)
                .getString("theme", DEFAULT)
        )
            recreate()
    }
    
    open fun getTheme(theme: String): Int {
        return when (theme) {
            AMOLED -> R.style.Theme_Overlayer_Amoled
            else -> R.style.Theme_Overlayer
        }
    }

    companion object {
        const val DEFAULT = "default"
        const val AMOLED = "amoled"
    }
}