package de.dertyp7214.overlayer.data

import android.graphics.drawable.Drawable

data class OverlayGroup(
    val name: String,
    val icon: Drawable?,
    val overlays: ArrayList<Overlay> = ArrayList(),
    var expanded: Boolean = false
)