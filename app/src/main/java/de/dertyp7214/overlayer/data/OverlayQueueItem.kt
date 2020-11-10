package de.dertyp7214.overlayer.data

data class OverlayQueueItem(val packageName: String, val state: Boolean, val refresh: Boolean, val clear: () -> Unit) {
    override fun toString(): String {
        return "packageName: $packageName\n" +
                "state: $state\n" +
                "refresh: $refresh\n" +
                "clear: $clear"
    }
}