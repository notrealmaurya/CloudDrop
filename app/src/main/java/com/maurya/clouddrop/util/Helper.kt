package com.maurya.clouddrop.util

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.maurya.clouddrop.database.AdapterLinks


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
        playStoreIntent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
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




