package de.dertyp7214.overlayer.core

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

fun Int.dp(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}