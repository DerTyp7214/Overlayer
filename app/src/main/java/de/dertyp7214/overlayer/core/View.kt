package de.dertyp7214.overlayer.core

import android.view.View
import android.view.ViewGroup

fun View.setHeight(height: Int) {
    if (layoutParams != null) layoutParams.height = height
    else layoutParams = ViewGroup.LayoutParams(width, height)
}

fun View.setMargin(
    leftMargin: Int? = null, topMargin: Int? = null,
    rightMargin: Int? = null, bottomMargin: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            leftMargin ?: params.leftMargin,
            topMargin ?: params.topMargin,
            rightMargin ?: params.rightMargin,
            bottomMargin ?: params.bottomMargin
        )
        layoutParams = params
    }
}