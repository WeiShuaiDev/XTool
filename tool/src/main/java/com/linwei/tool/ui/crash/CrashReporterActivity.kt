package com.linwei.tool.ui.crash

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.linwei.tool.R
import com.linwei.tool.XToolReporter
import com.linwei.tool.adapter.CrashViewPagerAdapter
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.FileUtils
import com.linwei.tool.utils.SaveUtil
import com.linwei.tool.utils.ThreadManager
import java.io.File

class CrashReporterActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mTab: TabLayout
    private lateinit var mViewpager: ViewPager

    private var mViewPagerAdapter: CrashViewPagerAdapter? = null
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
            title = getString(R.string.crash)
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

    override fun onDestroy() {
        super.onDestroy()
        mOnPageChangeListener?.let {
            mViewpager.removeOnPageChangeListener(it)
        }
    }


    private fun setupViewPager() {
        val mTitles = arrayOf(getString(R.string.crashes), getString(R.string.exceptions))
        mViewPagerAdapter = CrashViewPagerAdapter(supportFragmentManager, mTitles)
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