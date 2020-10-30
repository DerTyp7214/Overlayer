package de.dertyp7214.overlayer.core

import android.util.Log
import com.topjohnwu.superuser.Shell

fun String.runAsCommand(callback: (result: Array<String>) -> Unit = {}): Boolean {
    return Shell.su(this).exec().apply {
        if (err.size > 0) err.toTypedArray().apply { callback(this) }.contentToString()
        if (out.size > 0) out.toTypedArray().apply { callback(this) }.contentToString()
    }.isSuccess.apply {
        Log.d("RUN COMMAND", "${this@runAsCommand} -> $this")
    }
}