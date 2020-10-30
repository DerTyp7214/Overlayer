package de.dertyp7214.overlayer.helper

import android.content.Context
import android.os.Environment
import java.io.File

object FileHelper {
    fun getTmpPath(context: Context): File {
        return File(
            context.getExternalFilesDirs(Environment.DIRECTORY_NOTIFICATIONS)[0].absolutePath.removeSuffix(
                "Notifications"
            ), "Tmp"
        ).apply { if (!exists()) mkdirs() }
    }
}