package com.maurya.clouddrop.fragments

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import com.maurya.clouddrop.R
import com.maurya.clouddrop.database.AdapterLinks
import com.maurya.clouddrop.module.UploadResponse
import com.maurya.clouddrop.database.LinkDatabase
import com.maurya.clouddrop.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date


class HomeFragment : Fragment() {


    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var adapterLink: AdapterLinks


    private lateinit var db: LinkDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = fragmentHomeBinding.root


        listeners()



        return view
    }

    private fun listeners() {
        fragmentHomeBinding.manageYourLinksFileHomeFragment.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_linkFragment)
        }


    }


    private fun saveRecording(fileName: String) {
        val downloadsFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dtxVoicerecorderFolder = File(downloadsFolder, "dtxVoiceRecorder")

        val newFilePath = File(dtxVoicerecorderFolder, "$fileName.mp3")

        val fileSizeInBytes = newFilePath.length().toString()
        val fileSize = fileSizeInBytes
        val timeStamp = Date().time.toString()

        val record =
            UploadResponse(fileName, "", timeStamp)

        GlobalScope.launch {
            db.linkDao().insert(record)
        }

        adapterLink.notifyDataSetChanged()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        db = Room.databaseBuilder(
            requireContext(), LinkDatabase::class.java, "linkRecords"
        ).build()

    }


}