package de.dertyp7214.overlayer.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.adapters.OverlayAdapter
import de.dertyp7214.overlayer.core.getAttrColor
import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.overlayer.utils.doInBackground
import de.dertyp7214.overlayer.utils.getOverlays

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val swipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout) }

    private val adapter by lazy {
        OverlayAdapter(list, ::refresh)
    }

    private val list = ArrayList<OverlayGroup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        swipeRefreshLayout.setColorSchemeColors(
            getAttrColor(com.google.android.material.R.attr.colorPrimary),
            getAttrColor(com.google.android.material.R.attr.colorSecondary)
        )
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            getAttrColor(com.google.android.material.R.attr.colorSurface)
        )
        swipeRefreshLayout.setOnRefreshListener(::updateList)

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

    @SuppressLint("NotifyDataSetChanged")
    private fun updateList() {
        swipeRefreshLayout.isRefreshing = true
        doInBackground {
            list.clear()
            getOverlays().forEach {
                list.add(it)
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}