package org.nnt.infiniteviewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class InfiniteBannerPagerAdapter(fm: FragmentManager, private val data: List<Banner>) : InfinitePagerAdapter(fm, data){

    override fun getRealItem(position: Int): Fragment {
        return BannerFragment.newInstance(data[position])
    }
}