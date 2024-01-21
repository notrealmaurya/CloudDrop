package com.maurya.clouddrop.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maurya.clouddrop.databinding.LinkItemBinding


class AdapterLinks(
    private val context: Context,
    private var listener: OnItemClickListener,
    private val itemList: MutableList<DataLink> = mutableListOf()
) : RecyclerView.Adapter<AdapterLinks.ToDoFileHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoFileHolder {
        val binding = LinkItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ToDoFileHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoFileHolder, position: Int) {
        val currentItem = itemList[position]

        with(holder) {
            title.text=currentItem.title
            link.text=currentItem.link
            dateCreated.text=currentItem.createdAt

        }


    }


    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class ToDoFileHolder(binding: LinkItemBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        val title = binding.titleLinkItem
        val link = binding.downloadLinkLinkItem
        val dateCreated = binding.dateLinkItem
        private val root = binding.root

        init {
            root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickListener(position)
            }
        }

    }
}