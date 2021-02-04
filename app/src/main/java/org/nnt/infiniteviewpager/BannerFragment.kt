package org.nnt.infiniteviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class BannerFragment: Fragment() {

    private var banner: Banner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        banner = arguments?.getSerializable(BANNER) as Banner
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_banner, container, false)
        val imageView = view.findViewById<ImageView>(R.id.iv_banner)
        Glide.with(requireContext())
                .load(banner?.link)
                .into(imageView)
        return view
    }

    companion object {
        const val BANNER = "BANNER"
        fun newInstance(banner: Banner): BannerFragment {
            return BannerFragment().also {
                it.arguments = Bundle().apply {
                    putSerializable(BANNER, banner)
                }
            }
        }
    }
}