package com.maurya.clouddrop.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.maurya.clouddrop.database.AdapterLinks
import java.io.File
import java.io.IOException


fun shareContentWithFallback(
    shareIntent: Intent,
    packageName: String,
    fallbackMessage: String,
    holder: AdapterLinks.LinksHolder
) {
    try {
        shareIntent.`package` = packageName
        holder.itemView.context.startActivity(shareIntent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            holder.itemView.context,
            fallbackMessage,
            Toast.LENGTH_SHORT
        ).show()

        // Fallback to opening the Play Store link
        val playStoreIntent = Intent(Intent.ACTION_VIEW)
        playStoreIntent.data =
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        try {
            holder.itemView.context.startActivity(playStoreIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                holder.itemView.context,
                "No app found to handle the action",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


fun uriToFile(context: Context, uri: Uri, fileName: String): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, fileName) // Change to an appropriate file name

    if (inputStream != null) {
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    } else {
        val documentFile = DocumentFile.fromSingleUri(context, uri)
        if (documentFile != null && documentFile.isDirectory) {
            throw IOException("Selected item is a directory, not a file")
        } else {
            throw IOException("Could not open the file")
        }
    }

    return file
}


fun extractUuidFromLink(link: String): String {
    val parts = link.split("/")
    return parts.last()
}
