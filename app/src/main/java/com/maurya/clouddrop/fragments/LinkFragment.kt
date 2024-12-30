package com.maurya.clouddrop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maurya.clouddrop.databinding.FragmentLinkBinding
import com.maurya.clouddrop.database.AdapterLinks
import com.maurya.clouddrop.database.LinkDataClass
import com.maurya.clouddrop.database.LinkRepositoryForSavingInDB
import com.maurya.clouddrop.util.OnItemClickListener
import com.maurya.clouddrop.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LinkFragment : Fragment(), OnItemClickListener {

    private lateinit var fragmentLinkBinding: FragmentLinkBinding
    private lateinit var adapterLink: AdapterLinks

    private var linkList: ArrayList<LinkDataClass> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentLinkBinding = FragmentLinkBinding.inflate(inflater, container, false)
        val view = fragmentLinkBinding.root


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentLinkBinding.midLayout.isSelected = true

        linkList = arrayListOf()

        fragmentLinkBinding.recyclerViewLinksFragment.setHasFixedSize(true)
        fragmentLinkBinding.recyclerViewLinksFragment.setItemViewCacheSize(13)
        fragmentLinkBinding.recyclerViewLinksFragment.layoutManager =
            LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
        adapterLink = AdapterLinks(requireContext(), this, linkList)
        fragmentLinkBinding.recyclerViewLinksFragment.adapter = adapterLink


        updateRecyclerView()

        fragmentLinkBinding.backLinkFragment.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }


    override fun onItemLongClickListener(position: Int) {
        val itemToDelete = linkList[position]
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${itemToDelete.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    LinkRepositoryForSavingInDB.deleteSingleLink(itemToDelete.id)
                    withContext(Dispatchers.Main) {
                        linkList.removeAt(position)
                        adapterLink.notifyItemRemoved(position)
                    }
                }
                showToast(
                    requireContext(),
                    "'${itemToDelete.title}' deleted"
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun updateRecyclerView() {
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
        val currentTimeMillis = System.currentTimeMillis()

        lifecycleScope.launch {
            val allItems = LinkRepositoryForSavingInDB.getLinkFromDatabaseAsync()
            val itemsToRemove =
                allItems.filter { currentTimeMillis - it.createdAt > twentyFourHoursInMillis }
            for (record in itemsToRemove) {
                LinkRepositoryForSavingInDB.deleteSingleLink(record.id)
            }

            linkList.removeAll(itemsToRemove.toSet())
            withContext(Dispatchers.Main) {
                linkList.clear()
                linkList.addAll(LinkRepositoryForSavingInDB.getLinkFromDatabaseAsync())
                if (linkList.isEmpty()) {
                    fragmentLinkBinding.emptyRecyclerViewLinkFragment.visibility = View.VISIBLE
                } else {
                    fragmentLinkBinding.emptyRecyclerViewLinkFragment.visibility = View.GONE
                }
                adapterLink.notifyDataSetChanged()
            }
        }
    }


}