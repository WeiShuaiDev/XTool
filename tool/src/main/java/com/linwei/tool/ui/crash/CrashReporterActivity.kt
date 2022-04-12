package com.linwei.tool.ui.crash

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.linwei.tool.R
import com.linwei.tool.XToolReporter
import com.linwei.tool.adapter.CrashViewPagerAdapter
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.FileUtils
import com.linwei.tool.utils.SaveUtil
import com.linwei.tool.utils.ThreadManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File


class CrashReporterActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mTab: TabLayout
    private lateinit var mViewpager2: ViewPager2

    private var mViewPagerAdapter: CrashViewPagerAdapter? = null
    private var mSelectedTabPosition = 0

    private var mMediator: TabLayoutMediator? = null

    private val mTitles = arrayOf(R.string.crashes, R.string.exceptions)

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
        setContentView(R.layout.activity_reporter)
        mToolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            title = getString(R.string.crash)
            subtitle = getApplicationName()
        }
        setSupportActionBar(mToolbar)

        mTab = findViewById(R.id.tab)
        mViewpager2 = findViewById(R.id.viewpager2)

        setupViewPager()

        setupTabLayout()
    }

    private fun clearCrashLog() {
        ThreadManager.runOnThread {
            val crashReportPath =
                if (TextUtils.isEmpty(XToolReporter.getCrashReportPath())) SaveUtil.getDefaultPath(
                    Constants.CRASH_REPORT_DIR
                ) else XToolReporter.getCrashReportPath()
            val logs = File(crashReportPath).listFiles()
            for (file in logs) {
                FileUtils.delete(file)
            }
            runOnUiThread { mViewPagerAdapter?.clearLogs() }
        }
    }

    private fun setupTabLayout() {
        mMediator = TabLayoutMediator(
            mTab, mViewpager2
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
        mViewPagerAdapter = CrashViewPagerAdapter(this)
        mViewpager2.adapter = mViewPagerAdapter
        //viewPager 页面切换监听
        mViewpager2.registerOnPageChangeCallback(changeCallback)

        val intent = intent
        if (intent != null && !intent.getBooleanExtra(Constants.LANDING, false)) {
            mSelectedTabPosition = 0
        }
        mViewpager2.currentItem = mSelectedTabPosition
    }


    private val changeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mSelectedTabPosition = position
            //可以来设置选中时tab的大小
            val tabCount: Int = mTab.tabCount
            for (i in 0 until tabCount) {
                val tab: TabLayout.Tab? = mTab.getTabAt(i)
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
        mViewpager2.unregisterOnPageChangeCallback(changeCallback)
    }
}