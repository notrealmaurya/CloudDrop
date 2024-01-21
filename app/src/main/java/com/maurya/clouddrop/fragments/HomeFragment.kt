package com.maurya.clouddrop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.maurya.clouddrop.R
import com.maurya.clouddrop.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {


    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var navController: NavController

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
        fragmentHomeBinding.manageYourLinksFileHomeFragment.setOnClickListener{
            navController.navigate(R.id.action_homeFragment_to_linkFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


    }


}