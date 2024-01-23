package com.maurya.clouddrop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maurya.clouddrop.databinding.FragmentLinkBinding
import com.maurya.clouddrop.database.AdapterLinks
import com.maurya.clouddrop.model.UploadRequest
import com.maurya.clouddrop.model.UploadResponse
import com.maurya.clouddrop.util.OnItemClickListener


class LinkFragment : Fragment(), OnItemClickListener {

    private lateinit var fragmentLinkBinding: FragmentLinkBinding
    private lateinit var navController: NavController


    private lateinit var adapterLink: AdapterLinks

    companion object {
        var linkList: ArrayList<UploadRequest> = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentLinkBinding = FragmentLinkBinding.inflate(inflater, container, false)
        val view = fragmentLinkBinding.root


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        linkList = arrayListOf()

        fragmentLinkBinding.recyclerViewLinksFragment.setHasFixedSize(true)
        fragmentLinkBinding.recyclerViewLinksFragment.setItemViewCacheSize(13)
        fragmentLinkBinding.recyclerViewLinksFragment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterLink = AdapterLinks(requireContext(), this, linkList)
        fragmentLinkBinding.recyclerViewLinksFragment.adapter = adapterLink



        fragmentLinkBinding.backLinkFragment.setOnClickListener {
            findNavController().navigateUp()
        }

    }


    override fun onItemLongClickListener(position: Int) {
        val itemToDelete = linkList[position]

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${itemToDelete.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                linkList.removeAt(position)
                adapterLink.notifyDataSetChanged()
                Toast.makeText(
                    requireContext(),
                    "'${itemToDelete.title}' deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}