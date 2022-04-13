package com.linwei.tool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.linwei.tool.ui.crash.CrashLogFragment
import com.linwei.tool.ui.crash.ExceptionLogFragment

class CrashViewPagerAdapter(fragmentActivity: FragmentManager, val title: Array<String>) :
    FragmentPagerAdapter(fragmentActivity) {
    private var crashLogFragment: CrashLogFragment? = null
    private var exceptionLogFragment: ExceptionLogFragment? = null

    fun clearLogs() {
        crashLogFragment?.clearLog()
        exceptionLogFragment?.clearLog()
    }

    override fun getPageTitle(position: Int): CharSequence = title[position]

    override fun getCount(): Int = title.size

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            CrashLogFragment().also { crashLogFragment = it }
        } else if (position == 1) {
            ExceptionLogFragment().also { exceptionLogFragment = it }
        } else {
            CrashLogFragment()
        }
    }
}