package com.linwei.tool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.linwei.tool.ui.crash.CrashLogFragment
import com.linwei.tool.ui.crash.ExceptionLogFragment

class CrashViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private var crashLogFragment: CrashLogFragment? = null
    private var exceptionLogFragment: ExceptionLogFragment? = null

    fun clearLogs() {
        crashLogFragment?.clearLog()
        exceptionLogFragment?.clearLog()
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            CrashLogFragment().also { crashLogFragment = it }
        } else if (position == 1) {
            ExceptionLogFragment().also { exceptionLogFragment = it }
        } else {
            CrashLogFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}