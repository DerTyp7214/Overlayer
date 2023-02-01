package de.dertyp7214.overlayer.data

data class OverlayInfo(
    val name: String?,
    val packageName: String,
    val enabled: Boolean,
) {
    override fun toString(): String {
        return "[${if (enabled) "x" else " "}] ${name ?: packageName}"
    }

    val toggleCommand get() = if (enabled) disableCommand else enableCommand
    val enableCommand get() = "cmd overlay enable $packageName"
    val disableCommand get() = "cmd overlay disable $packageName"

    fun setHighPriority() = "cmd overlay set-priority $packageName highest"
    fun setLowPriority() = "cmd overlay set-priority $packageName lowest"
}