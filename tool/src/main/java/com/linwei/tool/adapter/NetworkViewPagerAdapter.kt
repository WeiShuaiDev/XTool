package com.linwei.tool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.linwei.tool.ui.crash.CrashLogFragment
import com.linwei.tool.ui.crash.ExceptionLogFragment
import com.linwei.tool.ui.network.AppLogFragment
import com.linwei.tool.ui.network.HttpLogFragment

class NetworkViewPagerAdapter(fragmentActivity: FragmentManager, val title: Array<String>) :
    FragmentPagerAdapter(fragmentActivity) {

    private var httpLogFragment: HttpLogFragment? = null
    private var appLogFragment: AppLogFragment? = null

    fun clearLogs() {
        httpLogFragment?.clearLog()
        appLogFragment?.clearLog()
    }

    override fun getPageTitle(position: Int): CharSequence = title[position]

    override fun getCount(): Int = title.size

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            HttpLogFragment().also { httpLogFragment = it }
        } else if (position == 1) {
            AppLogFragment().also { appLogFragment = it }
        } else {
            HttpLogFragment()
        }
    }
}