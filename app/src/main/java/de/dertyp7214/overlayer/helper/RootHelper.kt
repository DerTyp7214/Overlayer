package de.dertyp7214.overlayer.helper

import com.topjohnwu.superuser.Shell

object RootHelper {
    fun <E> runWithRoot(run: () -> E) {
        if (Shell.rootAccess()) {
            run()
        } else {
           Throwable("No root access!")
        }
    }
}