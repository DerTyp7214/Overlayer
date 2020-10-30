package de.dertyp7214.overlayer.helper

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.overlayer.Config.MAGISK_OVERLAY_PATH
import de.dertyp7214.overlayer.Config.MODULE_ID
import de.dertyp7214.overlayer.Config.OVERLAY_PATH
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.core.runAsCommand
import de.dertyp7214.overlayer.data.ModuleMeta
import de.dertyp7214.overlayer.data.Overlay
import de.dertyp7214.overlayer.data.OverlayGroup
import java.io.File

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
                        ContextCompat.getDrawable(context, R.drawable.no_icon)
                    }

                    map[lastGroupName]?.overlays?.add(Overlay(packageName, icon, enabled))
                } else if (!it.startsWith("---")) {
                    lastGroupName = it
                    val icon = try {
                        context.packageManager.getApplicationIcon(it)
                    } catch (e: Exception) {
                        ContextCompat.getDrawable(context, R.drawable.no_icon)
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

    fun installOverlay(overlay: File): Boolean {
        val installedOverlay: SuFile?
        if (MagiskHelper.isMagiskInstalled()) {
            Log.d("Install Overlay", "MAGISK WAY")
            if (!MagiskHelper.isModuleInstalled(MODULE_ID)) {
                val meta = ModuleMeta(
                    MODULE_ID,
                    "Overlayer",
                    "v1",
                    "1",
                    "RKBDI & DerTyp7214",
                    "Module for Overlayer app"
                )
                val file = mapOf(
                    Pair(OVERLAY_PATH, null)
                )
                MagiskHelper.installModule(meta, file)
            }
            installedOverlay = SuFile(MAGISK_OVERLAY_PATH, overlay.name)
            "mv ${overlay.absolutePath} ${installedOverlay.absolutePath}".runAsCommand {
                Log.d(
                    "MOVE OVERLAY",
                    it.contentToString()
                )
            }
        } else {
            Log.d("Install Overlay", "NON MAGISK WAY")
            installedOverlay = SuFile(OVERLAY_PATH, overlay.name)
            "mv ${overlay.absolutePath} ${installedOverlay.absolutePath}".runAsCommand {
                Log.d(
                    "MOVE OVERLAY",
                    it.contentToString()
                )
            }
        }

        return installedOverlay.exists()
    }
}