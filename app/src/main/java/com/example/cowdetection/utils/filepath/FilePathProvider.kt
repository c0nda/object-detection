package com.example.cowdetection.utils.filepath

import java.io.File

interface FilePathProvider {

    fun assetFilePath(asset: String): String

    fun ioDirectoryPath(): File

}