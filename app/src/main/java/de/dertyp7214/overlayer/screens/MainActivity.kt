package de.dertyp7214.overlayer.screens

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.ColorUtils
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.adapters.OverlayAdapter
import de.dertyp7214.overlayer.core.getAttrColor
import de.dertyp7214.overlayer.core.statusBarHeight
import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.overlayer.utils.doInBackground
import de.dertyp7214.overlayer.utils.getOverlays

class MainActivity : AppCompatActivity(), MenuProvider {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val swipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout) }
    private val fab by lazy { findViewById<FloatingActionButton>(R.id.fab) }
    private val bottomAppBar by lazy { findViewById<BottomAppBar>(R.id.bottomAppBar) }

    private val adapter by lazy {
        OverlayAdapter(this, list, ::refresh)
    }

    private val list = ArrayList<OverlayGroup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomAppBar.apply {
            val newTint = getAttrColor(com.google.android.material.R.attr.colorSurface)
            background = GradientDrawable().apply {
                colors = intArrayOf(ColorUtils.setAlphaComponent(newTint, 0xF8), newTint)
                gradientType = GradientDrawable.LINEAR_GRADIENT
                cornerRadius = 0f
            }
        }

        adapter.statusBarHeight = statusBarHeight()

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        swipeRefreshLayout.setProgressViewOffset(
            true,
            0,
            statusBarHeight()
        )
        swipeRefreshLayout.setColorSchemeColors(
            getAttrColor(com.google.android.material.R.attr.colorPrimary),
            getAttrColor(com.google.android.material.R.attr.colorSecondary)
        )
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            getAttrColor(com.google.android.material.R.attr.colorSurface)
        )
        swipeRefreshLayout.setOnRefreshListener(::updateList)

        bottomAppBar.addMenuProvider(this)

        fab.setOnClickListener {
            Snackbar.make(bottomAppBar, R.string.coming_soon, Snackbar.LENGTH_LONG).show()
        }

        updateList()
    }

    private fun refresh(parent: String, position: Int) {
        swipeRefreshLayout.isRefreshing = true
        doInBackground {
            getOverlays(parent).forEach {
                if (it.packageName == parent)
                    list[position] = it
            }
            runOnUiThread {
                adapter.notifyItemChanged(position)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateList() {
        swipeRefreshLayout.isRefreshing = true
        doInBackground {
            list.clear()
            getOverlays().forEach {
                list.add(it)
            }
            runOnUiThread {
                adapter.notifyData()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.app_bar_search).apply {
            isVisible = true
            val actionView = actionView
            if (actionView is SearchView) {
                actionView.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter(newText ?: "")
                        return false
                    }
                })
                actionView.setOnCloseListener {
                    adapter.clearFilter()
                    false
                }
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.app_bar_search -> {
                true
            }

            else -> false
        }
    }
}