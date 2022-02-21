package com.androrubin.reminderapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androrubin.reminderapp.adapters.VPAdapter
import com.androrubin.reminderapp.fragments.ReminderFragment
import com.androrubin.reminderapp.fragments.ToDoFragment
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?, ): View?
    {
        val view = layoutInflater.inflate(R.layout.fragment_home,container,false)

        val tabLayout = view?.findViewById<TabLayout>(R.id.tabLayout)
        val viewpager = view?.findViewById<CustomViewPager>(R.id.Viewpager)

        tabLayout?.setupWithViewPager(viewpager)

        val vpAdapter= VPAdapter(childFragmentManager)
        vpAdapter.addFragment(ReminderFragment(),"Reminders")
        vpAdapter.addFragment(ToDoFragment(),"To-Do List")
        viewpager?.adapter = vpAdapter
        viewpager?.setSwipePagingEnabled(false)
        return view
    }

}