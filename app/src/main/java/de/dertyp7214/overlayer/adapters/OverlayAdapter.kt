package de.dertyp7214.overlayer.adapters

import android.annotation.SuppressLint
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.overlay_group, parent, false)
    )

    override fun getItemCount() = overlays.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = overlays[position]

        when (position) {
            0 -> holder.cardView.corners(
                topLeftRadius = holder.itemView.context.getDimen(R.dimen.corner_radius),
                topRightRadius = holder.itemView.context.getDimen(R.dimen.corner_radius)
            )

            itemCount - 1 -> holder.cardView.corners(
                bottomLeftRadius = holder.itemView.context.getDimen(R.dimen.corner_radius),
                bottomRightRadius = holder.itemView.context.getDimen(R.dimen.corner_radius)
            )

            else -> holder.cardView.corners()
        }

        holder.icon.setImageDrawable(group.getIcon(holder.itemView.context))
        holder.label.text = group.getLabel(holder.itemView.context)
        holder.label.isSelected = true
        holder.overlayCount.text =
            "${group.overlays.size} overlay${if (group.overlays.size > 1) "s" else ""}"
        holder.overlayView.removeAllViews()
        group.overlays.forEach { overlay ->
            val view = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.overlay_item, holder.overlayView, false)
            view.findViewById<TextView>(R.id.label).text = group.getLabel(holder.itemView.context)
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