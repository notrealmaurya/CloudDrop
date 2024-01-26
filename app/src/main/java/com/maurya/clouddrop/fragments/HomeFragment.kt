package com.maurya.clouddrop.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maurya.clouddrop.R
import com.maurya.clouddrop.database.LinkDatabase
import com.maurya.clouddrop.databinding.FragmentHomeBinding
import com.maurya.clouddrop.model.DataDatabase
import com.maurya.clouddrop.repository.LinkRepository
import com.maurya.clouddrop.util.SharedPreferenceHelper
import com.maurya.clouddrop.util.showToast
import com.maurya.clouddrop.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {


    private lateinit var navController: NavController
    private lateinit var database: LinkDatabase
    private val themeList = arrayOf("Light Mode", "Dark Mode", "Auto")
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    fun getMyTextView(): TextView {
        return fragmentHomeBinding.downloadLinkHomeFragment
    }

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferenceHelper

    @Inject
    lateinit var linkRepository: LinkRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = fragmentHomeBinding.root



        sharedPreferencesHelper = SharedPreferenceHelper((requireContext()))

        database = Room.databaseBuilder(
            requireContext(), LinkDatabase::class.java, "linkRecords"
        ).build()

        fragmentHomeBinding.downloadLinkHomeFragment.isSelected = true



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
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Link: $textToCopy")
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


        fragmentHomeBinding.menuHomeFragment.setOnClickListener {

            val inflater =
                requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.menu_layout, null)

            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.showAtLocation(fragmentHomeBinding.menuHomeFragment, Gravity.TOP, 150, 400)

            val share = popupView.findViewById<LinearLayout>(R.id.shareLayoutPopUp)
            val feedback = popupView.findViewById<LinearLayout>(R.id.feedbackLayoutPopUp)
            val about = popupView.findViewById<LinearLayout>(R.id.aboutLayoutPopUp)
            val exit = popupView.findViewById<LinearLayout>(R.id.exitLayoutPopUp)
            val themeText = popupView.findViewById<TextView>(R.id.themeTextLayoutPopUp)
            val theme = popupView.findViewById<LinearLayout>(R.id.themeLayoutPopUp)

            var checkedTheme = sharedPreferencesHelper.theme
            themeText.text = themeList[sharedPreferencesHelper.theme]

            share.setOnClickListener {
                popupWindow.dismiss()
                val websiteUrl =
                    "https://github.com/notrealmaurya"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                startActivity(intent)
            }

            theme.setOnClickListener {
                popupWindow.dismiss()
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Change theme")
                    .setPositiveButton("Ok") { _, _ ->
                        sharedPreferencesHelper.theme = checkedTheme
                        AppCompatDelegate.setDefaultNightMode(sharedPreferencesHelper.themeFlag[checkedTheme])
                        themeText.text = themeList[sharedPreferencesHelper.theme]
                    }
                    .setSingleChoiceItems(themeList, checkedTheme) { _, which ->
                        checkedTheme = which
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
                dialog.setOnDismissListener {
                    dialog.dismiss()
                }


            }

            feedback.setOnClickListener {
                popupWindow.dismiss()
                val websiteUrl =
                    "https://forms.gle/4gC2XzHDCaio7hUh8"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                startActivity(intent)
            }

            about.setOnClickListener {
                popupWindow.dismiss()
                val customDialogFragment = AboutDialogFragment()
                customDialogFragment.show(childFragmentManager, "CustomDialogFragment")
            }

            exit.setOnClickListener {
                popupWindow.dismiss()
                requireActivity().finish()
            }


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
                fragmentHomeBinding.uploadFileLayoutHomeFragment.visibility = View.GONE
                fragmentHomeBinding.uploadedFileLayoutHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.emailLayoutHomeFragment.visibility= View.VISIBLE

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
                fragmentHomeBinding.uploadedFileLayoutHomeFragment.visibility = View.GONE
                fragmentHomeBinding.uploadFileLayoutHomeFragment.visibility = View.VISIBLE
                fragmentHomeBinding.emailLayoutHomeFragment.visibility= View.GONE

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