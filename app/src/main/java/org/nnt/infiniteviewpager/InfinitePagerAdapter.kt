package org.nnt.infiniteviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

abstract class InfinitePagerAdapter(fm: FragmentManager, private val data: List<Any>): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fakeCount = 0
    init {
        if(data.size>1){
            fakeCount = data.size*3
        }
        else fakeCount = data.size
    }
    override fun getCount(): Int {
        return fakeCount
    }
    fun getRealPosition(position: Int): Int{
        if(position>=getRealCount()&& position <getRealCount()*2){
            return position-getRealCount()
        }
        else if(position>=getRealCount()*2){
            return position-getRealCount()*2
        }
        else {
            return position
        }
    }
    fun getRealCount(): Int {
        return data.size
    }
    override fun getItem(position: Int): Fragment {
        return getRealItem(getRealPosition(position))
    }

    abstract fun getRealItem(position: Int): Fragment
}