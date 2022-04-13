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
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.linwei.tool.R
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
    private lateinit var mToolbar: Toolbar
    private lateinit var mTab: TabLayout
    private lateinit var mViewpager: ViewPager

    private var mViewPagerAdapter: NetworkViewPagerAdapter? = null
    private var mSelectedTabPosition = 0

    private var mOnPageChangeListener: ViewPager.OnPageChangeListener? = null

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
            title = getString(R.string.network)
            subtitle = getApplicationName()
        }
        setSupportActionBar(mToolbar)

        mTab = findViewById(R.id.tab)
        mViewpager = findViewById(R.id.viewpager)

        setupViewPager()

        mTab.setupWithViewPager(mViewpager)
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

    override fun onDestroy() {
        super.onDestroy()
        mOnPageChangeListener?.let {
            mViewpager.removeOnPageChangeListener(it)
        }
    }

    private fun setupViewPager() {
        val mTitles = arrayOf(getString(R.string.http),getString(R.string.logs))
        mViewPagerAdapter = NetworkViewPagerAdapter(supportFragmentManager,mTitles)
        mViewpager.adapter = mViewPagerAdapter
        //viewPager 页面切换监听
        mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mSelectedTabPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        }
        mViewpager.addOnPageChangeListener(mOnPageChangeListener!!)

        val intent = intent
        if (intent != null && !intent.getBooleanExtra(Constants.LANDING, false)) {
            mSelectedTabPosition = 0
        }
        mViewpager.currentItem = mSelectedTabPosition
    }

    private fun getApplicationName(): String {
        val applicationInfo = applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(
            stringId
        )
    }
}