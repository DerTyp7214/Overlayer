package de.dertyp7214.overlayer.helper

import com.jaredrummler.android.shell.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream
import de.dertyp7214.overlayer.Config.MODULES_PATH
import de.dertyp7214.overlayer.core.getString
import de.dertyp7214.overlayer.core.parseModuleMeta
import de.dertyp7214.overlayer.data.MagiskModule
import de.dertyp7214.overlayer.data.ModuleMeta

object MagiskHelper {
    fun isMagiskInstalled(): Boolean {
        val result = Shell.run("magisk")
        return result.getStderr().startsWith("magisk", true)
    }

    fun getMagiskVersionString(): String {
        val result = Shell.run("magisk -v")
        return result.getStdout()
    }

    fun getMagiskVersionNumber(): String {
        val result = Shell.run("magisk -V")
        return result.getStdout()
    }

    fun getMagiskVersionFullString(): String {
        val result = Shell.run("magisk -c")
        return result.getStdout()
    }

    fun getModules(): List<MagiskModule> {
        return if (isMagiskInstalled()) {
            SuFile(MODULES_PATH).listFiles()?.filter { SuFile(it, "module.prop").exists() }?.map {
                val meta = SuFile(it, "module.prop").parseModuleMeta()
                MagiskModule(meta.id, it, meta)
            } ?: ArrayList()
        } else ArrayList()
    }

    fun isModuleInstalled(id: String): Boolean {
        return getModules().any { it.id == id }
    }

    fun installModule(meta: ModuleMeta, files: Map<String, String?>) {
        RootHelper.runWithRoot {
            val moduleDir = SuFile(MODULES_PATH, meta.id)
            moduleDir.mkdirs()
            writeSuFile(SuFile(moduleDir, "module.prop"), meta.getString())
            files.forEach {
                SuFile(moduleDir, it.key).apply {
                    if (it.value != null) writeSuFile(this, it.value ?: "")
                    else mkdirs()
                }
            }
        }
    }

    private fun writeSuFile(file: SuFile, content: String) {
        SuFileOutputStream(file).use {
            it.write(content.toByteArray(Charsets.UTF_8))
        }
    }
}