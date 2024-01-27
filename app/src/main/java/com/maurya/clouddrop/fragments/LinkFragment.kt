package com.maurya.clouddrop.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maurya.clouddrop.databinding.FragmentLinkBinding
import com.maurya.clouddrop.database.AdapterLinks
import com.maurya.clouddrop.database.LinkDatabase
import com.maurya.clouddrop.model.DataDatabase
import com.maurya.clouddrop.util.OnItemClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LinkFragment : Fragment(), OnItemClickListener {

    private lateinit var fragmentLinkBinding: FragmentLinkBinding
    private lateinit var navController: NavController
    private lateinit var database: LinkDatabase
    private lateinit var adapterLink: AdapterLinks

    companion object {
        var linkList: ArrayList<DataDatabase> = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentLinkBinding = FragmentLinkBinding.inflate(inflater, container, false)
        val view = fragmentLinkBinding.root

        fragmentLinkBinding.midLayout.isSelected = true

        linkList = arrayListOf()

        database =
            Room.databaseBuilder(requireContext(), LinkDatabase::class.java, "linkRecords").build()


        fragmentLinkBinding.recyclerViewLinksFragment.setHasFixedSize(true)
        fragmentLinkBinding.recyclerViewLinksFragment.setItemViewCacheSize(13)
        fragmentLinkBinding.recyclerViewLinksFragment.layoutManager =
            LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
        adapterLink = AdapterLinks(requireContext(), this, linkList)
        fragmentLinkBinding.recyclerViewLinksFragment.adapter = adapterLink


        updateRecyclerView()


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

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
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    database.linkDao().delete(itemToDelete)
                    withContext(Dispatchers.Main) {
                        linkList.removeAt(position)
                        adapterLink.notifyDataSetChanged()
                    }
                }
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

    override fun onResume() {
        super.onResume()
        updateRecyclerView()
    }

    private fun updateRecyclerView() {

        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
        val currentTimeMillis = System.currentTimeMillis()
        GlobalScope.launch {
            val retrievedData = database.linkDao().getAll()
            val itemsToRemove =
                retrievedData.filter { currentTimeMillis - it.createdAt > twentyFourHoursInMillis }
            for (record in itemsToRemove) {
                database.linkDao().delete(record)
            }
            linkList.removeAll(itemsToRemove.toSet())
            withContext(Dispatchers.Main) {
                linkList.clear()
                linkList.addAll(retrievedData)
                if (linkList.isEmpty()) {
                    fragmentLinkBinding.emptyRecyclerViewLinkFragment.visibility = View.VISIBLE
                }
                adapterLink.notifyDataSetChanged()
            }
        }
    }


}