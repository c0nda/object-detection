package com.example.cowdetection.utils.filepath

import android.content.Context
import com.example.cowdetection.R
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FilePathProviderImpl @Inject constructor(
    private val context: Context
) : FilePathProvider {

    override fun assetFilePath(asset: String): String {
        val file = File(context.filesDir, asset)
        try {
            val inputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int

                while (true) {
                    read = inputStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun ioDirectoryPath(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile, context.resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }

}