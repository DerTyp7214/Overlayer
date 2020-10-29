package de.dertyp7214.overlayer.helper

import android.content.Context
import com.topjohnwu.superuser.Shell
import de.dertyp7214.overlayer.data.Overlay
import de.dertyp7214.overlayer.data.OverlayGroup

object OverlayHelper {
    fun getOverlays(context: Context): HashMap<String, OverlayGroup> {
        val map: HashMap<String, OverlayGroup> = HashMap()
        Shell.su("cmd overlay list").exec().apply {
            var lastGroupName: String? = null
            out.forEach {
                if (it.startsWith("[")) {
                    val enabled = it.startsWith("[x]")
                    val packageName = it.split("] ")[1]
                    val icon = map[lastGroupName]?.icon ?: try {
                        context.packageManager.getApplicationIcon(packageName)
                    } catch (e: Exception) {
                        null
                    }

                    map[lastGroupName]?.overlays?.add(Overlay(packageName, icon, enabled))
                } else if (!it.startsWith("---")) {
                    lastGroupName = it
                    val icon = try {
                        context.packageManager.getApplicationIcon(it)
                    } catch (e: Exception) {
                        null
                    }
                    if (it.isNotEmpty() && !map.containsKey(it)) map[it] =
                        OverlayGroup(it, icon)
                }
            }
        }

        return map
    }

    fun setOverlayState(packageName: String, state: Boolean): Boolean {
        return Shell.su("cmd overlay ${if (state) "enable" else "disable"} $packageName")
            .exec().isSuccess
    }
}