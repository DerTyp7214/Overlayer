package de.dertyp7214.overlayer.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.topjohnwu.superuser.Shell
import de.dertyp7214.overlayer.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            //Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER)

            if (Shell.rootAccess())
                startActivity(Intent(this, MainScreen::class.java))
            finish()
        }, 350)
    }
}