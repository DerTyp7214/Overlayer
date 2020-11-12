package de.dertyp7214.overlayer

import de.dertyp7214.rootutils.Magisk.Companion.MODULES_PATH

object Config {
    const val MODULE_ID = "overlayer"
    const val OVERLAY_PATH = "/system/app"

    val MAGISK_OVERLAY_PATH: String
        get() {
            return "$MODULES_PATH/$MODULE_ID$OVERLAY_PATH"
        }
}