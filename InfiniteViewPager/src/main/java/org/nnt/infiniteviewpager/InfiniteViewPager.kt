package org.nnt.infiniteviewpager

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class InfiniteViewPager: ViewPager {

    private var periodMillis = 3000L

    private var handler1 = Handler(Looper.getMainLooper())

    private var currentPage = 0

    private var isDragging = false

    private var mAdapter: PagerAdapter? = null
    private var position: Int? = null

    private val listener: OnPageChangeListener = object : OnPageChangeListener{
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            currentPage = position
            isDragging = false
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == SCROLL_STATE_IDLE) {
                isDragging = false
                adapter?.let {
                    if (it is InfinitePagerAdapter) {
                        val adapterCount = it.getRealCount()
                        if (currentPage < adapterCount) {
                            this@InfiniteViewPager.setCurrentItem(currentPage+adapterCount, false)
                            currentPage = currentItem
                        }
                        else if(currentPage >= adapterCount*2){
                            this@InfiniteViewPager.setCurrentItem(currentPage-adapterCount, false)
                            currentPage = currentItem
                        }
                    }
                }
            }
            if (state == SCROLL_STATE_DRAGGING) {
                isDragging = true
            }
        }

    }
    fun storeAdapter(adapter: PagerAdapter?, position: Int? = null){
        Log.d("AutoScrollViewPager", "onStoreAdapter")
        this.mAdapter = adapter
        this.position = position
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        this.mAdapter= adapter
        position = null
        Log.d("AutoScrollViewPager", "setAdapter")
        if(adapter is InfinitePagerAdapter){
            this.setCurrentItem(adapter.getRealCount(),false)
            this.offscreenPageLimit= adapter.getRealCount()+1
        }
        currentPage = currentItem
    }
    constructor(context: Context) : super(context) {
        val typedArray = context.obtainStyledAttributes(R.styleable.InfiniteViewPager)
        getAttribute(typedArray)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfiniteViewPager)
        getAttribute(typedArray)
    }
    private fun getAttribute(typeArray: TypedArray){
        periodMillis = typeArray.getInteger(R.styleable.InfiniteViewPager_period_millis, periodMillis.toInt()).toLong()
        typeArray.recycle()
    }

    private val timerTask = object : Runnable{
        override fun run() {
            if(!isDragging){
                val currentPage = this@InfiniteViewPager.currentItem
                val total = this@InfiniteViewPager.adapter?.count ?:0
                if (total >= 1) {
                    if (currentPage == total - 1) {
                        if (adapter is InfinitePagerAdapter) {
                            val count = (adapter as InfinitePagerAdapter).getRealCount()
                            this@InfiniteViewPager.setCurrentItem(count, true)
                        } else this@InfiniteViewPager.setCurrentItem(0, true)
                    } else {
                        this@InfiniteViewPager.setCurrentItem(currentItem + 1, true)
                    }
                }
            }
            handler1.postDelayed(this, periodMillis)
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("AutoScrollViewPager", "detach")
        this.removeOnPageChangeListener(listener)
        handler1.removeCallbacks(timerTask)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAdapter?.let{
            super.setAdapter(it)
            if(it is InfinitePagerAdapter){
                this.setCurrentItem(it.getRealCount(),false)
                this.offscreenPageLimit= it.getRealCount()+1
            }
            currentPage = currentItem
            position?.let {
                setCurrentItem(it, false)
            }
            this.addOnPageChangeListener(listener)
            handler1.postDelayed(timerTask, periodMillis)
        }
        Log.d("AutoScrollViewPager", "attach")
    }
}