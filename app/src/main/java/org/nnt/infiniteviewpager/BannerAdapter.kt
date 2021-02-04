package org.nnt.infiniteviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BannerAdapter(fm: FragmentManager, private val data: List<Banner>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//    private val fakeData: ArrayList<Banner> = ArrayList()
//    init {
//        if(data.size>1){
//            fakeData.addAll(data)
//            fakeData.addAll(data)
//            fakeData.addAll(data)
//        }
//        else fakeData.addAll(data)
//    }
    override fun getCount(): Int {
        return data.size
    }
//    fun getRealPosition(position: Int): Int{
//        if(position>=getRealCount()&& position <getRealCount()*2){
//            return position-getRealCount()
//        }
//        else if(position>=getRealCount()*2){
//            return position-getRealCount()*2
//        }
//        else {
//            return position
//        }
//    }
//    fun getRealCount(): Int {
//        return data.size
//    }

    override fun getItem(position: Int): Fragment {
        return BannerFragment.newInstance(data[position])
    }
}