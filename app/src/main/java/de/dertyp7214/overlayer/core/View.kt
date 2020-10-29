package de.dertyp7214.overlayer.core

import android.view.View
import android.view.ViewGroup

fun View.setHeight(height: Int) {
    if (layoutParams != null) layoutParams.height = height
    else layoutParams = ViewGroup.LayoutParams(width, height)
}