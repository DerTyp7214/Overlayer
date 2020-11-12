package de.dertyp7214.overlayer.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import com.topjohnwu.superuser.Shell
import de.dertyp7214.overlayer.BuildConfig
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.components.BaseActivity
import de.dertyp7214.rootutils.rootAccess

class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            //Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER)

            if (rootAccess() || BuildConfig.DEBUG) {
                startActivity(Intent(this, MainScreen::class.java))
                finish()
            } else {
                MaterialDialog(this).show {
                    setContentView(R.layout.no_root)

                    findViewById<MaterialButton>(R.id.button).setOnClickListener { finish() }
                }
            }
        }, 350)
    }

    override fun getTheme(theme: String): Int {
        return R.style.Theme_SplashScreen
    }
}