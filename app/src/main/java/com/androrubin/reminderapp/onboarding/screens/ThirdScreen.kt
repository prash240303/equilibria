package com.androrubin.reminderapp.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.androrubin.reminderapp.AboutApp
import com.androrubin.reminderapp.LoginActivity
import com.androrubin.reminderapp.R
import kotlinx.android.synthetic.main.fragment_third_screen.*
import kotlinx.android.synthetic.main.fragment_third_screen.view.*


class ThirdScreen : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)
        view.finish.setOnClickListener {

                findNavController().navigate(R.id.action_viewPagerFragment_to_loginActivity)
            onBoardingFinished()

        }
        return  view
    }

    private fun onBoardingFinished(){
       val setsharedPref = requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        val editor = setsharedPref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }

}