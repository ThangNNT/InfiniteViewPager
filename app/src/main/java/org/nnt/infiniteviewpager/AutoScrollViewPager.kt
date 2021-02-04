package org.nnt.infiniteviewpager

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class AutoScrollViewPager: ViewPager {

    private var periodMillis = 3000L

    private var handler1 = Handler(Looper.getMainLooper())

    private var currentPage = 0

    private val listener: OnPageChangeListener = object : OnPageChangeListener{
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            //Log.d("AAAAA", position.toString())
            currentPage = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            Log.d("AAAAA", "current page:" +currentPage.toString())
            if (state == SCROLL_STATE_IDLE) {
                //Log.d("AAAAA", "SCROLL_STATE_IDLE")
                handler1.removeCallbacks(timerTask)
                handler1.postDelayed(timerTask, periodMillis)
                adapter?.let {
                    if (it is InfinitePagerAdapter) {
                        val adapterCount = it.getRealCount()
                        if (currentPage < adapterCount) {
                            this@AutoScrollViewPager.setCurrentItem(currentPage+adapterCount, false)
                            //currentPage = currentItem
                        }
                        else if(currentPage >= adapterCount*2){
                           this@AutoScrollViewPager.setCurrentItem(currentPage-adapterCount, false)
                           // currentPage = currentItem
                        }
                    }
                }
            }
            if (state == SCROLL_STATE_DRAGGING) {
                Log.d("AAAAA", "SCROLL_STATE_DRAGGING")
                handler1.removeCallbacks(timerTask)
            }
        }

    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        currentPage = 0
        listener.let {
            this.removeOnPageChangeListener(it)
        }
        if(adapter is InfinitePagerAdapter){
            this.setCurrentItem(adapter.getRealCount(),false)
            currentPage = currentItem
        }
        this.addOnPageChangeListener(listener)
        handler1.removeCallbacks(timerTask)
        handler1.postDelayed(timerTask, periodMillis)
    }
    constructor(context: Context) : super(context) {
        val typedArray = context.obtainStyledAttributes(R.styleable.AutoScrollViewPager)
        getAttribute(typedArray)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollViewPager)
        getAttribute(typedArray)
    }
    private fun getAttribute(typeArray: TypedArray){
        periodMillis = typeArray.getInteger(R.styleable.AutoScrollViewPager_period_millis, periodMillis.toInt()).toLong()
        typeArray.recycle()
    }

    private val timerTask = object : Runnable{
        override fun run() {
            val currentPage = this@AutoScrollViewPager.currentItem
            val total = this@AutoScrollViewPager.adapter?.count ?:0
            if (total >= 1) {
                if (currentPage == total - 1) {
                    if (adapter is InfinitePagerAdapter) {
                        val count = (adapter as InfinitePagerAdapter).getRealCount()
                        this@AutoScrollViewPager.setCurrentItem(count, true)
                    } else this@AutoScrollViewPager.setCurrentItem(0, true)
                } else {
                    this@AutoScrollViewPager.setCurrentItem(currentItem + 1, true)
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler1.removeCallbacks(timerTask)
    }
}