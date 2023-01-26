package de.dertyp7214.overlayer.utils

import com.topjohnwu.superuser.Shell


@Suppress("DEPRECATION")
val hasRoot: Boolean
    get() {
        return try {
            Shell.su().exec()
            Shell.isAppGrantedRoot() == true
        } catch (e: Exception) {
            false
        }
    }