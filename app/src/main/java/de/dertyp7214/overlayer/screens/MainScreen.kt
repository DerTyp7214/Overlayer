package de.dertyp7214.overlayer.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.core.getFileName
import de.dertyp7214.overlayer.core.writeToFile
import de.dertyp7214.overlayer.fragments.OverlaysFragment
import de.dertyp7214.overlayer.helper.FileHelper
import de.dertyp7214.overlayer.helper.OverlayHelper
import de.dertyp7214.overlayer.viewmodel.OverlayViewModel
import kotlinx.android.synthetic.main.activity_main_screen.*
import java.io.File

class MainScreen : AppCompatActivity() {

    private lateinit var overlayViewModel: OverlayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        overlayViewModel = ViewModelProviders.of(this)[OverlayViewModel::class.java]

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

        search.setOnClickListener {
            MaterialDialog(this).show {
                setContentView(R.layout.search_popup)

                val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
                val cancelButton = findViewById<MaterialButton>(R.id.btn_cancel)
                val searchButton = findViewById<MaterialButton>(R.id.btn_search)

                fun search() {
                    val text = textInputLayout.editText?.text?.toString() ?: ""
                    overlayViewModel.setOverlays(
                        OverlayHelper.getOverlays(this@MainScreen)
                            .filter { it.value.name.contains(text, true) })
                    dismiss()
                }

                textInputLayout.editText?.setOnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                        search()
                        true
                    } else false
                }

                cancelButton.setOnClickListener { dismiss() }
                searchButton.setOnClickListener { search() }
            }
        }

        addOverlay.setOnClickListener {
            val intent = Intent()
                .setType("application/vnd.android.package-archive")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.select_overlay)),
                187
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 187 && resultCode == RESULT_OK && data?.data != null) {
            val overlay = File(FileHelper.getTmpPath(this), data.data!!.getFileName(this))
            data.data!!.writeToFile(this, overlay)
            OverlayHelper.installOverlay(overlay).apply { Log.d("OVERLAY INSTALLED", "$this") }
            overlayViewModel.setOverlays(OverlayHelper.getOverlays(this@MainScreen))
        }
    }
}