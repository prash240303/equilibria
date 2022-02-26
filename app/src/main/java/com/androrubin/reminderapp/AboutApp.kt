package com.androrubin.reminderapp

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_about_app.*
import me.relex.circleindicator.CircleIndicator
import me.relex.circleindicator.CircleIndicator3
import java.lang.Boolean


class AboutApp : AppCompatActivity() {


    // private var titleList = mutableListOf<String>()
    // private var descList = mutableListOf<String>()
    // private var imageList  = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   lateinit var itemArrayList: ArrayList<ViewPagerItem>
        setContentView(R.layout.activity_about_app)


    }
}
  /*  postToList()

    view_pager2.adapter = ViewPagerAdapter(titleList,descList,imageList)
    view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    //val indicator3 = findViewById<CircleIndicator3>(R.id.indicator)
    // indicator3.setViewPager(view_pager2)
    fake_swipe_btn.setOnClickListener{
        view_pager2.apply {
            beginFakeDrag()
            fakeDragBy(-10f)
            endFakeDrag()
        }
    }

}

private fun addToList(title: String ,description: String, image: Int)
{
    titleList.add(title)
    descList.add(description)
    imageList.add(image)

}


private  fun postToList(){
    for (i in 1..5){
        addToList("Title  $i", "Description $i", R.drawable.about1)
    }
}*/