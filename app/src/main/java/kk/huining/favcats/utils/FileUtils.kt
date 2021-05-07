package kk.huining.favcats.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.*


fun getContentType(context: Context, uri: Uri): MediaType? {
    var contentType : MediaType?
    contentType = context.contentResolver.getType(uri)?.toMediaTypeOrNull()
    if (contentType == null) {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        if (extension != null) {
            val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            contentType = type?.toMediaTypeOrNull()
        }
        // Below requires API 26
        //val path = fileFromContentUri(requireContext(), uri).toPath()
        //contentType = Files.probeContentType(path).toMediaTypeOrNull()
    }
    return contentType
}

fun fileFromContentUri(context: Context, contentUri: Uri): File {
    // Preparing Temp file name
    val fileExtension = getFileExtension(context, contentUri)
    val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""
    // Creating Temp file
    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()
    try {
        val outputStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)
        inputStream?.let {
            copy(inputStream, outputStream)
        }
        outputStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return tempFile
}

private fun getFileExtension(context: Context, uri: Uri): String? {
    val fileType: String? = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

