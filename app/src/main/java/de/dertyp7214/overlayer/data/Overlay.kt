package de.dertyp7214.overlayer.data

import android.graphics.drawable.Drawable

data class Overlay(
    val name: String,
    val packageName: String,
    val icon: Drawable?,
    var enabled: Boolean,
    var inQueue: Boolean = false
)