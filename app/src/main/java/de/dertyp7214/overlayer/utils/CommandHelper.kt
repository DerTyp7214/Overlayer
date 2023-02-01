package de.dertyp7214.overlayer.utils

import com.topjohnwu.superuser.Shell

@Suppress("DEPRECATION")
fun runCommand(command: String) = Shell.su(command).exec()