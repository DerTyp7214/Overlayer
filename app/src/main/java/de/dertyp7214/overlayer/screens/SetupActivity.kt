package de.dertyp7214.overlayer.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.dertyp7214.overlayer.R

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}