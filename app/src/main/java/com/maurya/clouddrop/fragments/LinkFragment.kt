package com.maurya.clouddrop.fragments

import android.os.Bundle
import android.util.Log
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
import com.maurya.clouddrop.util.Tags
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

        fragmentLinkBinding.recyclerViewLinksFragment.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(13)
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            adapterLink = AdapterLinks(requireContext(), this@LinkFragment, linkList)
            adapter = adapterLink
        }
        fragmentLinkBinding.backLinkFragment.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        LinkRepositoryForSavingInDB.getAllDBLink().observe(viewLifecycleOwner) { links ->
            Log.d(Tags.DBLinkRepo, "Loaded ${links.size} links from Room database")
            fragmentLinkBinding.recyclerViewLinksFragment.visibility = View.VISIBLE
            linkList.clear()
            linkList.addAll(links)
            adapterLink.notifyDataSetChanged()
        }

        lifecycleScope.launch {
            if (LinkRepositoryForSavingInDB.getLinkFromDatabaseAsync().isEmpty()) {
                fragmentLinkBinding.emptyRecyclerViewLinkFragment.visibility = View.VISIBLE
                fragmentLinkBinding.recyclerViewLinksFragment.visibility = View.GONE
            } else {
                fragmentLinkBinding.emptyRecyclerViewLinkFragment.visibility = View.GONE
            }
        }

    }


    override fun onItemLongClickListener(position: Int) {
        val itemToDelete = linkList[position]
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Delete Item")
            setMessage("Are you sure you want to delete '${itemToDelete.title}'?")
            setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            LinkRepositoryForSavingInDB.deleteSingleLink(itemToDelete.id)
                        }
                        linkList.removeAt(position)
                        adapterLink.notifyItemRemoved(position)
                        showToast(requireContext(), "'${itemToDelete.title}' deleted")

                    } catch (e: Exception) {
                        showToast(requireContext(), "Error deleting '${itemToDelete.title}'")
                    }
                }
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }


}