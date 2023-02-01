package de.dertyp7214.overlayer.core

import android.content.Context
import kotlin.math.roundToInt

fun Number.dp(context: Context): Float {
    return this.toFloat() * context.resources.displayMetrics.density
}

fun Number.dpRounded(context: Context): Int = dp(context).roundToInt()