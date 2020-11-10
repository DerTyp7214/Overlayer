package de.dertyp7214.overlayer.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.preference.ListPreference
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.dertyp7214.overlayer.R

class BottomSheetPreference(context: Context?, attributeSet: AttributeSet?) :
    ListPreference(context, attributeSet) {

    override fun onAttached() {
        super.onAttached()
        summary = entries[entryValues.indexOf(value)]
    }

    override fun onClick() {
        BottomSheetDialog(context, R.style.BottomSheetPreference).apply {
            setContentView(R.layout.bottomsheet_preference)
            val title = findViewById<TextView>(R.id.title)
            val list = findViewById<RecyclerView>(R.id.list)

            title?.text = this@BottomSheetPreference.title
            list?.apply {
                val items = ArrayList<Pair<String, String>>()
                entries.forEachIndexed { index, charSequence ->
                    items.add(Pair(charSequence.toString(), entryValues[index].toString()))
                }
                adapter = Adapter(context, value, items) {
                    callChangeListener(it)
                    value = it
                    summary = entries[entryValues.indexOf(it)]
                    dismiss()
                }
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }
        }.show()
    }

    private class Adapter(
        private val context: Context,
        private val currentValue: String,
        private val items: List<Pair<String, String>>,
        private val onItemClick: (value: String) -> Unit
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val root: ViewGroup = v.findViewById(R.id.root)
            val text: RadioButton = v.findViewById(R.id.textView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.bottom_sheet_preference_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            holder.text.text = item.first
            holder.text.isChecked = item.second == currentValue
            holder.text.setOnCheckedChangeListener { _, _ ->
                onItemClick(item.second)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}