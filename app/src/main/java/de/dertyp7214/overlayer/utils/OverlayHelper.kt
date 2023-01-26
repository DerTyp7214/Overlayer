package de.dertyp7214.overlayer.utils

import de.dertyp7214.overlayer.data.OverlayGroup
import de.dertyp7214.overlayer.data.OverlayInfo

fun getOverlays(packageName: String = ""): List<OverlayGroup> {
    val response = runCommand("cmd overlay list $packageName")

    if (!response.isSuccess) return emptyList()

    val packages = mutableListOf<OverlayGroup>()

    response.out.forEach {
        if (it.isNotBlank()) {
            if (it.startsWith("[")) {
                packages.last().overlays.add(
                    OverlayInfo(
                        it.substringAfter("]").trim(),
                        it.substringAfter("]").trim(),
                        it.startsWith("[x]")
                    )
                )
            } else {
                packages.add(
                    OverlayGroup(
                        it.substringAfter("]").trim(),
                        mutableListOf()
                    )
                )
            }
        }
    }

    return packages
}