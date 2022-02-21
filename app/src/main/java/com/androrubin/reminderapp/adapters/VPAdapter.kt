package com.androrubin.reminderapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class VPAdapter(fm: FragmentManager, behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) : FragmentPagerAdapter(fm, behavior) {


    private var  fragmentArrayList = ArrayList<Fragment>()
    private var fragmenttitle = ArrayList<String>()

    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentArrayList.get(position)
    }

    fun addFragment(fragment: Fragment, title:String){

        fragmentArrayList.add(fragment)
        fragmenttitle.add(title)

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmenttitle.get(position)
    }

}