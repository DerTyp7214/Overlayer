package de.dertyp7214.overlayer.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.core.dp
import de.dertyp7214.overlayer.core.setHeight
import de.dertyp7214.overlayer.data.Overlay
import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.overlayer.helper.OverlayHelper
import de.dertyp7214.overlayer.viewmodel.OverlayViewModel

class OverlaysFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_overlays, container, false)

        val overlayViewModel =
            ViewModelProviders.of(requireActivity())[OverlayViewModel::class.java]

        val recyclerView = v.findViewById<RecyclerView>(R.id.overlay_list)

        val list = ArrayList(overlayViewModel.getOverlays().toList().map { it.second }
            .filter { it.overlays.size > 0 })
        val adapter = OverlayGroupAdapter(requireActivity(), list)

        overlayViewModel.observeOverlays(this) { map ->
            list.clear()
            list.addAll(map.toList().map { it.second })
            adapter.notifyDataSetChanged()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            EndOffsetItemDecoration(55.dp(requireContext()))
        )

        adapter.notifyDataSetChanged()

        return v
    }
}

class OverlayGroupAdapter(private val activity: Activity, private val items: List<OverlayGroup>) :
    RecyclerView.Adapter<OverlayGroupAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val root: LinearLayout = v.findViewById(R.id.root)
        val name: TextView = v.findViewById(R.id.name)
        val icon: ImageView = v.findViewById(R.id.icon)
        val arrow: ImageView = v.findViewById(R.id.arrow)
        val overlayList: RecyclerView = v.findViewById(R.id.overlay_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.overlay_group_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val overlayGroup = items[position]

        holder.name.text = overlayGroup.name
        holder.icon.setImageDrawable(overlayGroup.icon)

        holder.arrow.rotation = if (overlayGroup.expanded) 0F else 180F

        val adapter = OverlayAdapter(activity, overlayGroup.overlays.sortedBy { it.packageName })

        holder.overlayList.layoutManager = LinearLayoutManager(activity)
        holder.overlayList.setHasFixedSize(true)
        holder.overlayList.adapter = adapter

        holder.overlayList.setHeight(if (overlayGroup.expanded) ViewGroup.LayoutParams.WRAP_CONTENT else 0)

        holder.root.setOnClickListener {
            overlayGroup.expanded = !overlayGroup.expanded

            val min = 0
            val max = (overlayGroup.overlays.size * 74).dp(activity)

            ObjectAnimator.ofFloat(
                holder.arrow,
                "rotation",
                if (overlayGroup.expanded) 0F else 180F
            ).start()
            ValueAnimator.ofInt(
                if (overlayGroup.expanded) min else max,
                if (overlayGroup.expanded) max else min
            ).apply {
                addUpdateListener {
                    holder.overlayList.setHeight(it.animatedValue as Int)
                    holder.overlayList.requestLayout()
                }
                start()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

class OverlayAdapter(private val activity: Activity, private val items: List<Overlay>) :
    RecyclerView.Adapter<OverlayAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.icon)
        val name: TextView = v.findViewById(R.id.name)
        val packageName: TextView = v.findViewById(R.id.packageName)
        val enabled: CheckBox = v.findViewById(R.id.enabled)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.overlay_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val overlay = items[position]

        holder.icon.setImageDrawable(overlay.icon)
        holder.name.text = overlay.name
        holder.packageName.text = overlay.packageName
        holder.enabled.isChecked = overlay.enabled

        holder.enabled.setOnClickListener {
            OverlayHelper.setOverlayState(overlay.packageName, holder.enabled.isChecked)
        }
    }

    override fun getItemCount(): Int = items.size
}