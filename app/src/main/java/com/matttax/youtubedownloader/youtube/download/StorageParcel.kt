package com.matttax.youtubedownloader.youtube.download

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

data class StorageParcel(
    val externalPath: String,
    val internalPath: String
)

fun StorageParcel.tryCopy(): Boolean {
    return try {
        Files.move(Path(externalPath), Path(internalPath), StandardCopyOption.REPLACE_EXISTING)
        true
    }
    catch (ex: Exception) { false }
}
