package de.dertyp7214.overlayer.data

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build

data class OverlayGroup(
    val packageName: String,
    val overlays: MutableList<OverlayInfo>,
) {
    private var icon: Drawable? = null
    private var label: String? = null

    fun getIcon(context: Context): Drawable? {
        if (icon == null) {
            icon = context.packageManager.getApplicationIcon(packageName)
        }
        return icon
    }

    @Suppress("DEPRECATION")
    fun getLabel(context: Context): String {
        if (label == null) {
            label = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                context.packageManager.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.ApplicationInfoFlags.of(0)
                    )
                ).toString()
            else
                context.packageManager.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        packageName,
                        0
                    )
                ).toString()

        }
        return label ?: packageName
    }
}