package org.nnt.infiniteviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

abstract class InfinitePagerAdapter(fm: FragmentManager, private val data: List<Any>?): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fakeCount = 0
    init {
        data?.let {
            fakeCount = if(data.size>1){
                data.size*3
            } else data.size
        }
    }
    override fun getCount(): Int {
        return fakeCount
    }
    fun getRealPosition(position: Int): Int{
        return if(position>=getRealCount()&& position <getRealCount()*2){
            position-getRealCount()
        } else if(position>=getRealCount()*2){
            position-getRealCount()*2
        } else {
            position
        }
    }
    fun getRealCount(): Int {
        return data?.size?:0
    }
    override fun getItem(position: Int): Fragment {
        return getRealItem(getRealPosition(position))
    }

    abstract fun getRealItem(position: Int): Fragment
}