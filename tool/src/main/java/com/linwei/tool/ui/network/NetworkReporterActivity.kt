package com.linwei.tool.ui.network

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.linwei.tool.R
import com.linwei.tool.databinding.ActivityReporterBinding
import com.linwei.tool.XToolReporter
import com.linwei.tool.adapter.NetworkViewPagerAdapter
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.FileUtils
import com.linwei.tool.utils.SaveUtil
import com.linwei.tool.utils.ThreadManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File


class NetworkReporterActivity: AppCompatActivity() {
    private lateinit var mBinding: ActivityReporterBinding

    private var mViewPagerAdapter: NetworkViewPagerAdapter? = null
    private var mSelectedTabPosition = 0

    private var mMediator: TabLayoutMediator? = null

    private val mTitles = arrayOf(R.string.http, R.string.logs)

    private val mActiveColor: Int = Color.parseColor("#FFFFFF")
    private val mNormalColor: Int = Color.parseColor("#AAAAAA")

    private val mActiveSize = 20f
    private val mNormalSize = 16f

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.log_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.delete_crash_logs) {
            clearCrashLog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityReporterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.title = getString(R.string.network)
        mBinding.toolbar.subtitle = getApplicationName()
        setSupportActionBar(mBinding.toolbar)

        setupViewPager()

        setupTabLayout()
    }

    private fun clearCrashLog() {
        ThreadManager.runOnThread {
            val crashReportPath =
                if (TextUtils.isEmpty(XToolReporter.getNetworkReportPath())) SaveUtil.getDefaultPath(
                    Constants.NETWORK_REPORT_DIR) else XToolReporter.getNetworkReportPath()
            val logs = File(crashReportPath).listFiles()
            for (file in logs) {
                FileUtils.delete(file)
            }
            runOnUiThread { mViewPagerAdapter?.clearLogs() }
        }
    }

    private fun setupTabLayout(){
        mMediator = TabLayoutMediator(
            mBinding.tab, mBinding.viewpager2
        ) { tab, position -> //这里可以自定义TabView
            val tabView = TextView(this)
            val states = arrayOfNulls<IntArray>(2)
            states[0] = intArrayOf(android.R.attr.state_selected)
            states[1] = intArrayOf()
            val colors = intArrayOf(mActiveColor, mNormalColor)
            val colorStateList = ColorStateList(states, colors)
            tabView.text = getString(mTitles.get(position))
            tabView.textSize = mNormalSize
            tabView.setTextColor(colorStateList)
            tab.customView = tabView
        }
        mMediator?.attach()
    }


    private fun setupViewPager() {
        mViewPagerAdapter = NetworkViewPagerAdapter(this)
        mBinding.viewpager2.adapter = mViewPagerAdapter
        //viewPager 页面切换监听
        mBinding.viewpager2.registerOnPageChangeCallback(changeCallback)

        val intent = intent
        if (intent != null && !intent.getBooleanExtra(Constants.LANDING, false)) {
            mSelectedTabPosition = 0
        }
        mBinding.viewpager2.currentItem = mSelectedTabPosition
    }


    private val changeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mSelectedTabPosition = position
            //可以来设置选中时tab的大小
            val tabCount: Int = mBinding.tab.tabCount
            for (i in 0 until tabCount) {
                val tab: TabLayout.Tab? = mBinding.tab.getTabAt(i)
                if (tab != null) {
                    val tabView = tab.customView as TextView
                    if (tab.position == position) {
                        tabView.textSize = mActiveSize
                        tabView.setTypeface(Typeface.DEFAULT_BOLD)
                    } else {
                        tabView.textSize = mNormalSize
                        tabView.setTypeface(Typeface.DEFAULT)
                    }
                }
            }
        }
    }

    private fun getApplicationName(): String {
        val applicationInfo = applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(
            stringId
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.viewpager2.unregisterOnPageChangeCallback(changeCallback)
    }

}