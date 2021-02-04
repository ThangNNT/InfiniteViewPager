package org.nnt.infiniteviewpager

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import java.util.*

class MainActivity : AppCompatActivity() {
    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 500
    private val PERIOD_MS: Long = 3000
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var data = ArrayList<Banner>()
    private var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById<ViewPager>(R.id.view_pager)
        data.add(Banner("https://img.freepik.com/free-photo/jasmine-flower-greenery_34266-955.jpg?size=626&ext=jpg"))
        data.add(Banner("https://images-na.ssl-images-amazon.com/images/I/71zNWbTHzxL._SX679_.jpg"))
        data.add(Banner("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/mothers-day-flower-bouquet-1588610191.jpg"))

        viewPager?.adapter = InfiniteBannerPagerAdapter(supportFragmentManager, data)
       // viewPager?.setCurrentItem(data.size, false)
        //currentPage = data.size
        //var currentPosition = data.size
//        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//                currentPosition = position
//            }
//
//            override fun onPageSelected(position: Int) {
//                Log.d("AAAAA", position.toString())
//                currentPosition = position
//                stopSwipe()
//                startSwipe()
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                //Log.d("AAAAAA", state.toString())
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    viewPager?.adapter?.let {
//                        if (it is InfinitePagerAdapter) {
//                            if (currentPosition < data.size) {
//                                viewPager?.setCurrentItem(currentPosition+data.size, false)
//                                currentPage = currentPosition
//                            }
//                            else if(currentPosition >= data.size*2){
//                                viewPager?.setCurrentItem(currentPosition-data.size, false)
//                                currentPage = currentPosition
//                            }
////                            if (currentPosition == 0) {
////                                viewPager.setCurrentItem(data.size, false)
////                            } else if (currentPosition == data.size * 3 - 1) {
////                                viewPager.setCurrentItem(data.size - 1, false)
////                            }
//                        }
//                    }
//                }
//                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//                    stopSwipe();
//                    startSwipe();
//                }
//            }
//        })
//        val handler = Handler()
//        val update = Runnable {
//            if (currentPage == data.size*3-1) {
//                currentPage = 0;
//            }
//            viewPager?.setCurrentItem(currentPage++, true);
//        }
//
//        timer = Timer() // This will create a new Thread
//        timer?.schedule(object : TimerTask() {
//            // task to be scheduled
//            override fun run() {
//                handler.post(update)
//            }
//        }, DELAY_MS, PERIOD_MS)
    }
    private fun startSwipe(){
        handler = Handler()
        runnable = Runnable {
            //Log.d("XXXXX",currentPage.toString())
            if (currentPage == data.size*3-1) {
                currentPage = 0
            }
            viewPager?.setCurrentItem(currentPage++, true)
            runnable?.let {
                handler?.postDelayed(it, PERIOD_MS)
            }
        }
        runnable?.let {
            handler?.postDelayed(it, PERIOD_MS)
        }
    }

    private fun stopSwipe(){
        runnable?.let {
            handler?.removeCallbacks(it)
        }
    }

    override fun onPause() {
        super.onPause()
       // stopSwipe()
    }

    override fun onResume() {
        super.onResume()
        //startSwipe()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}