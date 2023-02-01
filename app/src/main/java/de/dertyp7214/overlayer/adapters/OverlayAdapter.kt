package de.dertyp7214.overlayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.core.corners
import de.dertyp7214.overlayer.core.getDimen
import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.overlayer.utils.runCommand

class OverlayAdapter(
    private val context: Context,
    private val overlays: List<OverlayGroup>,
    private val refresh: (parent: String, position: Int) -> Unit
) : RecyclerView.Adapter<OverlayAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        val icon: ImageView = view.findViewById(R.id.icon)
        val label: TextView = view.findViewById(R.id.label)
        val overlayCount: TextView = view.findViewById(R.id.overlayCount)
        val indicator: ImageView = view.findViewById(R.id.indicator)
        val overlayView: LinearLayout = view.findViewById(R.id.overlays)
    }

    private val expanded = HashMap<Int, Boolean>()
    private val filtered = HashMap<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.overlay_group, parent, false)
    )

    private var firstFiltered = 0
    private var lastFiltered = itemCount - 1

    @SuppressLint("NotifyDataSetChanged")
    fun notifyData() {
        notifyDataSetChanged()
        firstFiltered = 0
        lastFiltered = itemCount - 1
    }

    fun filter(query: String = "") {
        firstFiltered = 0
        lastFiltered = itemCount - 1

        val changedIndex = ArrayList<Int>()
        val allIndexes = ArrayList<Int>()
        overlays.forEachIndexed { index, overlayGroup ->
            allIndexes.add(index)
            val oldF = filtered[index]
            val oldE = expanded[index]
            val label = overlayGroup.getLabel(context).contains(query, true)
            val item = overlayGroup.overlays.any { it.packageName.contains(query, true) }
            filtered[index] = label || item
            expanded[index] = item && !label
            if (oldF != filtered[index] || oldE != expanded[index]) changedIndex.add(index)
        }
        allIndexes.forEach {
            if (changedIndex.contains(it)) notifyItemChanged(it)
            fun notifyLowest(index: Int = 1) {
                if (it == 0 && !changedIndex.contains(index)) {
                    notifyItemChanged(index)
                } else if (it == 0) notifyLowest(index + 1)
            }

            fun notifyHighest(index: Int = itemCount - 2) {
                if (it == itemCount - 1 && !changedIndex.contains(index)) {
                    notifyItemChanged(index)
                } else if (it == itemCount - 1) notifyHighest(index - 1)
            }
            notifyLowest()
            notifyHighest()
        }

        if (query.isNotEmpty()) {
            firstFiltered = filtered.filter { it.value }.keys.minOrNull() ?: 0
            lastFiltered = filtered.filter { it.value }.keys.maxOrNull() ?: (itemCount - 1)
            notifyItemChanged(firstFiltered)
            notifyItemChanged(lastFiltered)
        }
    }

    fun clearFilter() {
        val changedIndex = ArrayList<Int>()
        overlays.forEachIndexed { index, _ ->
            if (filtered[index] == true || expanded[index] == true) changedIndex.add(index)
            filtered[index] = false
            expanded[index] = false
        }
        changedIndex.forEach { notifyItemChanged(it) }
        firstFiltered = 0
        lastFiltered = itemCount - 1
    }

    override fun getItemCount(): Int {
        return overlays.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = overlays[position]

        if (filtered.any { it.value } && filtered[position] != true) {
            holder.cardView.visibility = View.GONE
            return
        } else holder.cardView.visibility = View.VISIBLE

        when {
            position == firstFiltered && (position == lastFiltered || lastFiltered == -1) -> holder.cardView.corners(
                context.getDimen(R.dimen.corner_radius),
                context.getDimen(R.dimen.corner_radius),
                context.getDimen(R.dimen.corner_radius),
                context.getDimen(R.dimen.corner_radius)
            )

            position == firstFiltered -> holder.cardView.corners(
                topLeftRadius = context.getDimen(R.dimen.corner_radius),
                topRightRadius = context.getDimen(R.dimen.corner_radius)
            )

            position == lastFiltered -> holder.cardView.corners(
                bottomLeftRadius = context.getDimen(R.dimen.corner_radius),
                bottomRightRadius = context.getDimen(R.dimen.corner_radius)
            )

            else -> holder.cardView.corners()
        }

        holder.icon.setImageDrawable(group.getIcon(context))
        holder.label.text = group.getLabel(context)
        holder.label.isSelected = true
        holder.overlayCount.text =
            "${group.overlays.size} overlay${if (group.overlays.size > 1) "s" else ""}"
        holder.overlayView.removeAllViews()
        group.overlays.sortedBy { it.packageName }.forEach { overlay ->
            val view = LayoutInflater.from(context)
                .inflate(R.layout.overlay_item, holder.overlayView, false)
            view.findViewById<TextView>(R.id.label).text = group.getLabel(context)
            view.findViewById<TextView>(R.id.packageName).apply {
                text = overlay.packageName
                isSelected = true
            }
            view.findViewById<MaterialCheckBox>(R.id.checkBox).apply {
                isChecked = overlay.enabled
                setOnCheckedChangeListener { _, isChecked ->
                    val command = if (!isChecked) overlay.disableCommand else overlay.enableCommand
                    if (runCommand(command).isSuccess) refresh(
                        group.packageName, position
                    )
                }
            }
            holder.overlayView.addView(view)
        }

        if (expanded[position] == true) {
            holder.overlayView.visibility = View.VISIBLE
            holder.indicator.rotation = 0f
        } else {
            holder.overlayView.visibility = View.GONE
            holder.indicator.rotation = 180f
        }

        holder.cardView.setOnClickListener {
            if (holder.overlayView.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(holder.cardView)
                holder.overlayView.visibility = View.GONE
                holder.indicator.rotation = 180f
                expanded[position] = false
            } else {
                TransitionManager.beginDelayedTransition(holder.cardView)
                holder.overlayView.visibility = View.VISIBLE
                holder.indicator.rotation = 0f
                expanded[position] = true
            }
        }
    }
}