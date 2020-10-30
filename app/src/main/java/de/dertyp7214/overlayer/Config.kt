package de.dertyp7214.overlayer

object Config {
    const val MODULES_PATH = "/data/adb/modules"
    const val MODULE_ID = "overlayer"
    const val OVERLAY_PATH = "/system/app"

    val MAGISK_OVERLAY_PATH: String
        get() {
            return "$MODULES_PATH/$MODULE_ID$OVERLAY_PATH"
        }
}