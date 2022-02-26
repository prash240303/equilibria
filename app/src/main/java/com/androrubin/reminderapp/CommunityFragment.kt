package com.androrubin.reminderapp

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity


class CommunityFragment : Fragment() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<Community>
    lateinit var imageId: Array<Int>
    lateinit var heading: Array<String>
    lateinit var chat: Array<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        imageId = arrayOf(
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
            R.drawable.f,
            R.drawable.g,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
            R.drawable.f
        )

        heading = arrayOf(
            "Astro",
            "Photography",
            "Music",
            "Book Hub",
            "Phoenix",
            "Coding",
            "Shades",
            "Wall Street Club",
            "Report Writing",
            "Tech Enthusiasts",
            "Anime"
        )

        chat = arrayOf(
            getString(R.string.chat1),
            getString(R.string.chat2),
            getString(R.string.chat3),
            getString(R.string.chat4),
            getString(R.string.chat5),
            getString(R.string.chat6),
            getString(R.string.chat7)
        )

    }

    fun onFabClick(view: View){
        Toast.makeText(context,"Chat Clicked", Toast.LENGTH_LONG).show()
    }

    private fun getUserdata() {
        for (i in imageId.indices){
            val community  = Community(imageId[i], heading[i])
            newArrayList.add(community)
        }
        var adapter = CommunityAdapter(newArrayList)
        newRecyclerView.adapter = adapter

//        newRecyclerView.adapter = CommunityAdapter(newArrayList)
        adapter.setOnItemClickListener(object : CommunityAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(getActivity(),ChatActivity::class.java)
                intent.putExtra("chat",heading[position])
                getActivity()?.startActivity(intent)

            }

        })


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)
        // Inflate the layout for this fragment
        newRecyclerView = view.findViewById(R.id.recylerView1)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<Community>()
        getUserdata()
        return view
    }



}