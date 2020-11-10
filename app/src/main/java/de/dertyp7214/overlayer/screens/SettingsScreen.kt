package de.dertyp7214.overlayer.screens

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration
import com.dgreenhalgh.android.simpleitemdecoration.linear.StartOffsetItemDecoration
import de.dertyp7214.overlayer.BuildConfig
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.components.BaseActivity
import de.dertyp7214.overlayer.core.dp
import kotlinx.android.synthetic.main.activity_settings_screen.*
import kotlinx.android.synthetic.main.info_layout.*

class SettingsScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_screen)

        floatingActionButton.setOnClickListener {
            floatingActionButton.isExpanded = true
        }

        version.text = BuildConfig.VERSION_NAME

        val list = arrayListOf(
            Item(
                getString(R.string.github),
                getString(R.string.github_sub)
            ) {
                CustomTabsIntent.Builder().apply {
                    TypedValue().apply {
                        theme.resolveAttribute(R.attr.colorPrimary, this, true)
                        setToolbarColor(data)
                    }
                }.build().launchUrl(this, Uri.parse(getString(R.string.github_url)))
            }
        )

        val adapter = Adapter(this, list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(StartOffsetItemDecoration(30.dp(this)))
        recyclerView.addItemDecoration(EndOffsetItemDecoration(30.dp(this)))
    }

    override fun getTheme(theme: String): Int {
        return when (theme) {
            AMOLED -> R.style.Theme_Overlayer_Settings_Amoled
            else -> R.style.Theme_Overlayer_Settings
        }
    }

    override fun onBackPressed() {
        if (floatingActionButton.isExpanded) floatingActionButton.isExpanded = false
        else super.onBackPressed()
    }

    private data class Item(val title: String, val subTitle: String, val onClick: () -> Unit)

    private class Adapter(private val context: Context, private val items: ArrayList<Item>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val parent: ViewGroup = v.findViewById(R.id.parent)
            val title: TextView = v.findViewById(R.id.title)
            val subTitle: TextView = v.findViewById(R.id.subTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.info_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            holder.title.text = item.title
            holder.subTitle.text = item.subTitle

            holder.parent.setOnClickListener { item.onClick() }
        }

        override fun getItemCount(): Int = items.size
    }
}