package org.nnt.infiniteviewpager

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.parcel.Parcelize

class InfiniteViewPager: ViewPager {

    /** define period time to change page **/
    private var delayMillis = 3000L

    private var handler1 = Handler(Looper.getMainLooper())

    private var currentPage = 0

    private var isAutoScroll = false

    private var isDragging = false

    private var mAdapter: PagerAdapter? = null
    private var position: Int? = null

    constructor(context: Context) : super(context) {
        val typedArray = context.obtainStyledAttributes(R.styleable.InfiniteViewPager)
        getAttribute(typedArray)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfiniteViewPager)
        getAttribute(typedArray)
    }
    private fun getAttribute(typeArray: TypedArray){
        delayMillis = typeArray.getInteger(R.styleable.InfiniteViewPager_nnt_delay_millis, delayMillis.toInt()).toLong()
        isAutoScroll = typeArray.getBoolean(R.styleable.InfiniteViewPager_nnt_autoScroll, false)
        typeArray.recycle()
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        if(isAttachedToWindow){
            super.setAdapter(adapter)
            this.mAdapter= adapter
            position = null
            Log.d("InfiniteViewPager", "setAdapter")
            if(adapter is InfinitePagerAdapter){
                this.setCurrentItem(adapter.getRealCount(), false)
                this.offscreenPageLimit= adapter.getRealCount()+1
            }
            currentPage = currentItem
        }
        else {
            this.mAdapter = adapter
        }
    }

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
                            this@InfiniteViewPager.setCurrentItem(currentPage + adapterCount, false)
                            currentPage = currentItem
                        }
                        else if(currentPage >= adapterCount*2){
                            this@InfiniteViewPager.setCurrentItem(currentPage - adapterCount, false)
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
            handler1.postDelayed(this, delayMillis)
        }

    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d("InfiniteViewPager", "onSave")
        val bundle = Bundle().apply {
            currentPage.let {
                putInt(CURRENT_POSITION, it)
            }
            mAdapter?.let {
                putParcelable(ADAPTER, it.saveState())
            }
            putBoolean(IS_AUTO_SCROLL, isAutoScroll)
        }
        return InfiniteSaveState(bundle, SavedState(super.onSaveInstanceState() as SavedState))
    }
    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d("InfiniteViewPager", "onRestore")
        if (state is InfiniteSaveState) {
            super.onRestoreInstanceState(state.savedState.superState)
            position = state.bundle.getInt(CURRENT_POSITION)
            isAutoScroll = state.bundle.getBoolean(IS_AUTO_SCROLL)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAdapter?.let{ adapter ->
            super.setAdapter(adapter)
            if(adapter is InfinitePagerAdapter){
                this.setCurrentItem(adapter.getRealCount(), false)
                this.offscreenPageLimit= adapter.getRealCount()+1
            }
            currentPage = currentItem
            position?.let {
                setCurrentItem(it, false)
            }
            this.addOnPageChangeListener(listener)
            if(isAutoScroll){
                handler1.postDelayed(timerTask, delayMillis)
            }
        }
        Log.d("InfiniteViewPager", "attach")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("InfiniteViewPager", "detach")
        this.removeOnPageChangeListener(listener)
        handler1.removeCallbacks(timerTask)
    }

    fun setAutoScroll(isAutoScroll: Boolean){
        if(isAutoScroll){
            if(!this.isAutoScroll){
                this.isAutoScroll = isAutoScroll
                handler1.postDelayed(timerTask, delayMillis)
            }
        }
        else {
            if(this.isAutoScroll){
                this.isAutoScroll = isAutoScroll
                handler1.removeCallbacks(timerTask)
            }
        }
    }


    @Parcelize
    data class InfiniteSaveState(var bundle: Bundle, var savedState: SavedState): Parcelable

    companion object {
        private const val CURRENT_POSITION = "CURRENT_POSITION"
        private const val ADAPTER = "ADAPTER"
        private const val IS_AUTO_SCROLL = "IS_AUTO_SCROLL"
    }
}