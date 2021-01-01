package com.hmju.memo.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Description : ViewPager2
 * Fragment 전용 Base PagerAdapter Class
 *
 * Created by hmju on 2020-12-30
 */
abstract class BaseFragmentPagerAdapter<Type>(ctx: Context) : FragmentStateAdapter(ctx as FragmentActivity) {

    abstract fun onCreateFragment(pos: Int) : Fragment

    var dataList = arrayListOf<Type>()
        set(value) {
            field.clear()
            field.addAll(value)

            notifyDataSetChanged()
        }

    override fun getItemCount() = dataList.size

    override fun createFragment(position: Int): Fragment {
        return onCreateFragment(position)
    }
}