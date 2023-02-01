package de.dertyp7214.overlayer.data

data class CommandResult(
    val command: String,
    val exitCode: Int,
    val output: String,
    val error: String
)
