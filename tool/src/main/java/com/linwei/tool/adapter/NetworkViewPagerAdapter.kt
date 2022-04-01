package com.linwei.tool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.linwei.tool.ui.network.AppLogFragment
import com.linwei.tool.ui.network.HttpLogFragment

class NetworkViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private var httpLogFragment: HttpLogFragment? = null
    private var appLogFragment: AppLogFragment? = null

    fun clearLogs() {
        httpLogFragment?.clearLog()
        appLogFragment?.clearLog()
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            HttpLogFragment().also { httpLogFragment = it }
        } else if (position == 1) {
            AppLogFragment().also { appLogFragment = it }
        } else {
            HttpLogFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}