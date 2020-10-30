package de.dertyp7214.overlayer.core

import de.dertyp7214.overlayer.data.ModuleMeta

fun ModuleMeta.getString(): String {
    return "id=$id\nname=$name\nversion=$version\nversionCode=$versionCode\nauthor=$author\ndescription=$description"
}