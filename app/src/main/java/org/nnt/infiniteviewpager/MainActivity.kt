package org.nnt.infiniteviewpager

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private var data = ArrayList<Banner>()
    private var viewPager: InfiniteViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.view_pager)
        data.add(Banner("https://img.freepik.com/free-photo/jasmine-flower-greenery_34266-955.jpg?size=626&ext=jpg"))
        data.add(Banner("https://images-na.ssl-images-amazon.com/images/I/71zNWbTHzxL._SX679_.jpg"))
        data.add(Banner("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/mothers-day-flower-bouquet-1588610191.jpg"))

        viewPager?.adapter = InfiniteBannerPagerAdapter(supportFragmentManager, data)

        var isAutoScroll = false
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            isAutoScroll = !isAutoScroll
            viewPager?.setAutoScroll(isAutoScroll)
        }

    }


}