package de.dertyp7214.overlayer.helper

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.overlayer.BuildConfig
import de.dertyp7214.overlayer.Config.MAGISK_OVERLAY_PATH
import de.dertyp7214.overlayer.Config.MODULE_ID
import de.dertyp7214.overlayer.Config.OVERLAY_PATH
import de.dertyp7214.overlayer.R
import de.dertyp7214.overlayer.data.Overlay
import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.rootutils.Magisk
import de.dertyp7214.rootutils.asCommand
import de.dertyp7214.rootutils.su
import java.io.File

object OverlayHelper {
    fun getOverlays(context: Context): HashMap<String, OverlayGroup> {
        val map: HashMap<String, OverlayGroup> = HashMap()
        su("cmd overlay list").exec().apply {
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

                    map[lastGroupName]?.overlays?.add(
                        Overlay(
                            packageName.removePrefix("$lastGroupName."),
                            packageName,
                            icon,
                            enabled
                        )
                    )
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

        if (BuildConfig.DEBUG) map[BuildConfig.APPLICATION_ID] = OverlayGroup(
            context.getString(R.string.app_name),
            ContextCompat.getDrawable(context, R.mipmap.ic_launcher)
        ).apply {
            overlays.add(
                Overlay(
                    "Overlay",
                    "${BuildConfig.APPLICATION_ID}_overlay",
                    icon,
                    false
                )
            )
        }

        return map
    }

    fun setOverlayState(packageName: String, state: Boolean): Boolean {
        return "cmd overlay ${if (state) "enable" else "disable"} $packageName".asCommand() || BuildConfig.DEBUG
    }

    fun installOverlay(overlay: File): Boolean {
        val installedOverlay: SuFile?
        val magisk = Magisk.getMagisk()
        if (magisk != null) {
            Log.d("Install Overlay", "MAGISK WAY")
            if (!magisk.isModuleInstalled(MODULE_ID)) {
                val meta = Magisk.Module.Meta(
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
                magisk.installOrUpdateModule(meta, file)
            }
            installedOverlay = SuFile(MAGISK_OVERLAY_PATH, overlay.name)
            "mv ${overlay.absolutePath} ${installedOverlay.absolutePath}".asCommand {
                Log.d(
                    "MOVE OVERLAY",
                    it.contentToString()
                )
            }
        } else {
            Log.d("Install Overlay", "NON MAGISK WAY")
            installedOverlay = SuFile(OVERLAY_PATH, overlay.name)
            "mv ${overlay.absolutePath} ${installedOverlay.absolutePath}".asCommand {
                Log.d(
                    "MOVE OVERLAY",
                    it.contentToString()
                )
            }
        }

        return installedOverlay.exists()
    }
}