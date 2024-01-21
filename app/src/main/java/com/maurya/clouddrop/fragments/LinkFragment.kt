package com.maurya.clouddrop.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.maurya.clouddrop.R
import com.maurya.clouddrop.databinding.FragmentLinkBinding
import com.maurya.clouddrop.util.AdapterLinks
import com.maurya.clouddrop.util.OnItemClickListener
import com.maurya.clouddrop.util.DataLink


class LinkFragment : Fragment(), OnItemClickListener {

    private lateinit var fragmentLinkBinding: FragmentLinkBinding
    private lateinit var navController: NavController


    private lateinit var adapterNotes: AdapterLinks

    companion object {
        lateinit var linkList: MutableList<DataLink>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentLinkBinding = FragmentLinkBinding.inflate(inflater, container, false)
        val view = fragmentLinkBinding.root


        listener()



        return view
    }

    private fun listener() {
        fragmentLinkBinding.backLinkFragment.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


        val linkList = mutableListOf(
            DataLink("Link 1", "https://www.example.com/1", "2022-01-21"),
            DataLink("Link 2", "https://www.example.com/2", "2022-01-22"),
            DataLink("Link 3", "https://www.example.com/3", "2022-01-23")
        )

        fragmentLinkBinding.recyclerViewLinksFragment.setHasFixedSize(true)
        fragmentLinkBinding.recyclerViewLinksFragment.setItemViewCacheSize(13)
        fragmentLinkBinding.recyclerViewLinksFragment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterNotes = AdapterLinks(requireContext(), this, linkList)
        fragmentLinkBinding.recyclerViewLinksFragment.adapter = adapterNotes
    }


    override fun onItemClickListener(position: Int) {
        Toast.makeText(requireContext(),"ItemCLicked + $position",Toast.LENGTH_SHORT).show()
    }


}