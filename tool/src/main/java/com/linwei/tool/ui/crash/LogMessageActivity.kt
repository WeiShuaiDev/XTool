package com.linwei.tool.ui.crash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.linwei.tool.utils.AppUtils
import androidx.core.content.FileProvider
import com.linwei.tool.R
import com.linwei.tool.databinding.ActivityLogMessageBinding
import com.linwei.tool.utils.FileUtils
import java.io.File

class LogMessageActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityLogMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLogMessageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.toolbar.title = getString(R.string.crash)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dirPath = intent.getStringExtra("LogMessage")
        dirPath?.let {
            val file = File(dirPath)
            val crashLog = FileUtils.readFromFile(file)
            mBinding.logMessage.text = crashLog
        }

        mBinding.appInfo.text = AppUtils.getDeviceDetails(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.crash_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = intent
        var filePath: String? = null
        if (intent != null) {
            filePath = intent.getStringExtra("LogMessage")
        }
        filePath?.let {
            return if (item.itemId == R.id.delete_log) {
                if (FileUtils.delete(it)) {
                    finish()
                }
                true
            } else if (item.itemId == R.id.share_crash_log) {
                shareCrashReport(it)
                true
            } else {
                super.onOptionsItemSelected(item)
            }
        }
       return super.onOptionsItemSelected(item)
    }

    private fun shareCrashReport(filePath: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TEXT, mBinding.appInfo.text.toString())
        val uriForFile =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
        intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}