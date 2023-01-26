package de.dertyp7214.overlayer.screens

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import de.dertyp7214.overlayer.R

class SetupActivity : AppCompatActivity() {

    private val granted = MutableLiveData<Boolean>()

    private val requestPermissionResultContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            this.granted.value = granted
        }

    private val btnRoot by lazy { findViewById<MaterialButton>(R.id.btnRoot) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        requestPermissionResultContract.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        btnRoot.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}