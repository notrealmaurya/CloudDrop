package com.maurya.clouddrop.database

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.maurya.clouddrop.databinding.LinkItemBinding
import com.maurya.clouddrop.model.UploadRequest
import com.maurya.clouddrop.model.UploadResponse
import com.maurya.clouddrop.util.OnItemClickListener
import com.maurya.clouddrop.util.shareContentWithFallback


class AdapterLinks(
    private val context: Context,
    private var listener: OnItemClickListener,
    private val itemList: MutableList<UploadRequest> = mutableListOf()
) : RecyclerView.Adapter<AdapterLinks.LinksHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksHolder {
        val binding = LinkItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return LinksHolder(binding)
    }

    override fun onBindViewHolder(holder: LinksHolder, position: Int) {
        val currentItem = itemList[position]

        with(holder) {
            title.text = currentItem.title
            link.text = currentItem.link
            dateCreated.text = currentItem.createdAt


            sharePopUpOption.visibility = View.GONE

            shareButton.setOnClickListener {
                val isVisible = sharePopUpOption.visibility == View.VISIBLE
                sharePopUpOption.visibility = if (isVisible) View.GONE else View.VISIBLE
            }


            shareWhatsApp.setOnClickListener {
                shareContent("WhatsApp", currentItem.title, currentItem.link, holder)
            }

            shareFacebook.setOnClickListener {
                shareContent("Facebook", currentItem.title, currentItem.link, holder)
            }

            shareMessage.setOnClickListener {
                shareContent("Message", currentItem.title, currentItem.link, holder)
            }

            shareCopyLink.setOnClickListener {
                val clipboard =
                    root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Link", currentItem.link)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(root.context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            shareMore.setOnClickListener {
                shareContent("More", currentItem.title, currentItem.link, holder)
            }


        }


    }

    private fun shareContent(appName: String, title: String, link: String, holder: LinksHolder) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "$title: $link")
        shareIntent.type = "text/plain"

        when (appName) {
            "WhatsApp" -> {
                shareIntent.`package` = "com.whatsapp"
                shareContentWithFallback(
                    shareIntent,
                    "com.whatsapp",
                    "WhatsApp not installed",
                    holder
                )
            }

            "Facebook" -> {
                shareIntent.`package` = "com.facebook.katana"
                shareContentWithFallback(
                    shareIntent,
                    "com.facebook.katana",
                    "Facebook not installed",
                    holder
                )

            }

            else -> {

                try {
                    holder.itemView.context.startActivity(
                        Intent.createChooser(shareIntent, "Share using $appName")
                    )
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        holder.itemView.context,
                        "No app found to handle the share action",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class LinksHolder(binding: LinkItemBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnLongClickListener {
        val title = binding.titleLinkItem
        val link = binding.downloadLinkLinkItem
        val dateCreated = binding.dateLinkItem
        val shareButton = binding.linkShareButtonLinkItem
        val sharePopUpOption = binding.sharePopUpOptionLayoutLinkItem
        val shareWhatsApp = binding.whatsAppShareLinkItem
        val shareFacebook = binding.faceBookShareLinkItem
        val shareMessage = binding.messageShareLinkItem
        val shareCopyLink = binding.copyLinkShareLinkItem
        val shareMore = binding.moreShareLinkItem


        val root = binding.root

        init {
            root.setOnLongClickListener(this)
        }


        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClickListener(position)
            }
            return true
        }

    }
}