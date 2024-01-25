package com.maurya.clouddrop.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import com.maurya.clouddrop.R
import com.maurya.clouddrop.database.AdapterLinks
import com.maurya.clouddrop.database.LinkDatabase
import com.maurya.clouddrop.databinding.FragmentHomeBinding
import com.maurya.clouddrop.model.DataDatabase
import com.maurya.clouddrop.repository.LinkRepository
import com.maurya.clouddrop.util.showToast
import com.maurya.clouddrop.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {


    private lateinit var navController: NavController
    private lateinit var database: LinkDatabase

    private lateinit var fragmentHomeBinding: FragmentHomeBinding


    @Inject
    lateinit var linkRepository: LinkRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = fragmentHomeBinding.root

        database = Room.databaseBuilder(
            requireContext(), LinkDatabase::class.java, "linkRecords"
        ).build()

        val currentTimeMillis = System.currentTimeMillis()
        val currentDateTime = Date(currentTimeMillis)

        val homeList =
            DataDatabase("Title1", "https://google.com", currentDateTime.time)

        GlobalScope.launch {
            database.linkDao().insert(homeList)
            Log.d("MyDatabaseLog", database.linkDao().getAll().count().toString())
        }



        listeners()
        return view
    }

    private fun listeners() {

        fragmentHomeBinding.manageYourLinksFileHomeFragment.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_linkFragment)
        }

        fragmentHomeBinding.uploadFileLayoutHomeFragment.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, 123)
        }

        fragmentHomeBinding.downloadLinkHomeFragment.setOnClickListener {
            val textToCopy = fragmentHomeBinding.downloadLinkHomeFragment.text.toString()
            val clipboardManager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            showToast(requireContext(), "Text copied to clipboard")
        }

        fragmentHomeBinding.linkShareButtonHomeFragment.setOnClickListener {
            val textToCopy = fragmentHomeBinding.downloadLinkHomeFragment.text.toString()
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Title: $textToCopy")
            shareIntent.type = "text/plain"

            try {
                requireContext().startActivity(
                    Intent.createChooser(shareIntent, "Share using ")
                )
            } catch (ex: ActivityNotFoundException) {
                showToast(requireContext(), "No app found to handle the share action")
            }
        }


        fragmentHomeBinding.cancelUploadedFile.setOnClickListener {
            fragmentHomeBinding.uploadedFileLayoutHomeFragment.visibility = View.GONE
            fragmentHomeBinding.uploadFileLayoutHomeFragment.visibility = View.VISIBLE
            fragmentHomeBinding.uploadingProgressLayoutFileHomeFragment.visibility = View.GONE
        }

    }

    private fun handleFileUploadAndLinkGeneration(selectedFile: File) {

        lifecycleScope.launch {
            try {
                linkRepository.uploadFile(selectedFile)
                fragmentHomeBinding.uploadingProgressLayoutFileHomeFragment.visibility =
                    View.VISIBLE
                fragmentHomeBinding.UploadingDownloadLinkText.text = "Download Link :"
                fragmentHomeBinding.seekBarHomeFragment.visibility = View.GONE
                fragmentHomeBinding.downloadLinkHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.linkShareButtonHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.uploadedFileLayoutHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.uploadFileLayoutHomeFragment.visibility = View.VISIBLE

                showToast(requireContext(), "File uploaded successfully")

                val currentTimeMillis = System.currentTimeMillis()
                val currentDateTime = Date(currentTimeMillis)
                val linkSave =
                    DataDatabase(
                        selectedFile.name,
                        fragmentHomeBinding.downloadLinkHomeFragment.text.toString(),
                        currentDateTime.time
                    )

                GlobalScope.launch {
                    database.linkDao().insert(linkSave)
                }


            } catch (e: Exception) {
                fragmentHomeBinding.uploadingProgressLayoutFileHomeFragment.visibility = View.GONE
                fragmentHomeBinding.downloadLinkHomeFragment.visibility = View.GONE
                fragmentHomeBinding.linkShareButtonHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.uploadedFileLayoutHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.uploadFileLayoutHomeFragment.visibility = View.VISIBLE

                showToast(requireContext(), "Error uploading file: ${e.message}")
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileNameFromUri(uri)
                val selectedFile = uriToFile(requireContext(), uri, fileName)
                handleFileUploadAndLinkGeneration(selectedFile)
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown_file"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


    }


}