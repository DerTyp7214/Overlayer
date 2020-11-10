package de.dertyp7214.overlayer.screens

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.components.BaseActivity
import de.dertyp7214.overlayer.core.dp
import de.dertyp7214.overlayer.core.getFileName
import de.dertyp7214.overlayer.core.writeToFile
import de.dertyp7214.overlayer.fragments.OverlaysFragment
import de.dertyp7214.overlayer.helper.FileHelper
import de.dertyp7214.overlayer.helper.OverlayHelper
import de.dertyp7214.overlayer.viewmodel.OverlayViewModel
import kotlinx.android.synthetic.main.activity_main_screen.*
import java.io.File

class MainScreen : BaseActivity() {

    private lateinit var overlayViewModel: OverlayViewModel

    private var lastState = HashMap<Int, Boolean>()
    private var searching = false
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val toolbarMain = findViewById<Toolbar>(R.id.toolbarMain)

        overlayViewModel = ViewModelProviders.of(this)[OverlayViewModel::class.java]

        supportFragmentManager.beginTransaction().apply {
            replace(fragment.id, OverlaysFragment())
            commit()

            Handler(Looper.getMainLooper()).postDelayed({
                overlayViewModel.setOverlays(OverlayHelper.getOverlays(this@MainScreen))
            }, 200)
        }

        settings.setOnClickListener {
            startActivity(
                Intent(this, SettingsScreen::class.java),
                ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    addOverlay,
                    "fab"
                ).toBundle()
            )
        }

        overlayViewModel.observeOverlayQueue(this) {
            if (searching) toolbarMain.title =
                "$searchText${if (it.isNotEmpty()) " (${it.size})" else ""}"
            else toolbarMain.title = getString(R.string.changeOverlay, it.size.toString())
            Handler(Looper.getMainLooper()).postDelayed({
                changeToolbarVisibility(toolbarMain, searching || it.isNotEmpty())
                if (searching)
                    toolbarMain.navigationIcon =
                        ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
                else if (it.isNotEmpty())
                    toolbarMain.navigationIcon =
                        ContextCompat.getDrawable(this, R.drawable.ic_close)
                if (it.isEmpty())
                    toolbarMain.menu.iterator().forEach { item -> item.isVisible = false }
                else
                    toolbarMain.menu.iterator().forEach { item -> item.isVisible = true }
            }, if (it.isNotEmpty()) 100 else 0)
        }

        toolbarMain.setTitleTextColor(getColor(R.color.title))
        toolbarMain.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        toolbarMain.setNavigationOnClickListener {
            if (searching) {
                overlayViewModel.setOverlays(OverlayHelper.getOverlays(this@MainScreen))
                searching = false
                changeToolbarVisibility(
                    toolbarMain,
                    searching || overlayViewModel.getOverlayQueue().isNotEmpty()
                )
                toolbarMain.title = getString(
                    R.string.changeOverlay,
                    overlayViewModel.getOverlayQueue().size.toString()
                )
                if (overlayViewModel.getOverlayQueue().isNotEmpty()) {
                    toolbarMain.navigationIcon =
                        ContextCompat.getDrawable(this, R.drawable.ic_close)
                    toolbarMain.menu.iterator().forEach { it.isVisible = true }
                } else
                    toolbarMain.menu.iterator().forEach { item -> item.isVisible = false }
            } else {
                overlayViewModel.clearOverlays()
                toolbarMain.menu.iterator().forEach { it.isVisible = false }
            }
        }
        toolbarMain.setOnMenuItemClickListener {
            if (it.itemId == R.id.applyOverlays) {
                overlayViewModel.getOverlayQueue()
                    .forEach { (t, u) -> OverlayHelper.setOverlayState(t, u.state) }
                overlayViewModel.clearOverlays()
            }
            true
        } // TODO: fix this shit

        search.setOnClickListener {
            MaterialDialog(this).show {
                setContentView(R.layout.search_popup)

                val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
                val cancelButton = findViewById<MaterialButton>(R.id.btn_cancel)
                val searchButton = findViewById<MaterialButton>(R.id.btn_search)

                textInputLayout.editText?.setText(searchText)

                fun search() {
                    val text = textInputLayout.editText?.text?.toString() ?: ""
                    overlayViewModel.setOverlays(
                        OverlayHelper.getOverlays(this@MainScreen)
                            .filter { it.value.name.contains(text, true) })
                    dismiss()
                    searchText = text
                    searching = searchText.isNotEmpty()
                    changeToolbarVisibility(
                        toolbarMain,
                        searching || overlayViewModel.getOverlayQueue().isNotEmpty()
                    )
                    toolbarMain.title = "$text${
                        if (overlayViewModel.getOverlayQueue()
                                .isNotEmpty()
                        ) " (${overlayViewModel.getOverlayQueue().size})" else ""
                    }"
                    if (searching) {
                        toolbarMain.navigationIcon =
                            ContextCompat.getDrawable(this@MainScreen, R.drawable.ic_arrow_back)
                        toolbarMain.menu.iterator().forEach { it.isVisible = false }
                    } else if (overlayViewModel.getOverlayQueue().isNotEmpty())
                        toolbarMain.menu.iterator().forEach { it.isVisible = true }
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

    private fun changeToolbarVisibility(toolbar: Toolbar, visible: Boolean) {
        if (lastState[toolbar.id] != visible) {
            lastState[toolbar.id] = visible
            ValueAnimator.ofFloat(if (!visible) 1F else 0F, if (visible) 1F else 0F).apply {
                addUpdateListener {
                    toolbar.alpha = it.animatedValue as Float
                }
                if (!visible)
                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationRepeat(animation: Animator?) {}

                        override fun onAnimationEnd(animation: Animator?) {
                            toolbar.visibility = View.GONE
                        }
                    })
                else
                    toolbar.visibility = View.VISIBLE
                start()
            }
            if (fragment.layoutParams is ViewGroup.MarginLayoutParams) {
                val toolbarHeightAttr = intArrayOf(android.R.attr.actionBarSize)
                val index = 0
                val typedArray = obtainStyledAttributes(TypedValue().data, toolbarHeightAttr)
                val toolbarHeight = typedArray.getDimensionPixelSize(index, -1)
                typedArray.recycle()
                ValueAnimator.ofInt(
                    if (!visible) toolbarHeight else 0,
                    if (visible) toolbarHeight else 0
                ).apply {
                    addUpdateListener {
                        (fragment.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                            0,
                            it.animatedValue as Int,
                            0,
                            70.dp(this@MainScreen)
                        )
                        fragment.requestLayout()
                    }
                    startDelay = 100
                    start()
                }
            }
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