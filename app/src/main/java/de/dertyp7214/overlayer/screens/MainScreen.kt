package de.dertyp7214.overlayer.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.fragments.OverlaysFragment
import de.dertyp7214.overlayer.helper.OverlayHelper
import de.dertyp7214.overlayer.viewmodel.OverlayViewModel
import kotlinx.android.synthetic.main.activity_main_screen.*

class MainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val overlayViewModel = ViewModelProviders.of(this)[OverlayViewModel::class.java]

        supportFragmentManager.beginTransaction().apply {
            replace(fragment.id, OverlaysFragment())
            commit()

            Handler(Looper.getMainLooper()).postDelayed({
                overlayViewModel.setOverlays(OverlayHelper.getOverlays(this@MainScreen))
            }, 200)
        }

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsScreen::class.java))
        }
    }
}