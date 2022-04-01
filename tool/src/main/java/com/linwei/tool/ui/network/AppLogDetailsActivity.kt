package com.linwei.tool.ui.network

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.linwei.tool.R
import com.linwei.tool.bean.AppLog
import com.linwei.tool.bean.HttpLog
import com.linwei.tool.databinding.ActivityAppLogDetailsBinding
import com.linwei.tool.databinding.ActivityHttpLogDetailsBinding
import com.linwei.tool.utils.FileUtils
import java.io.File
import java.util.*

class AppLogDetailsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAppLogDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAppLogDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.title = getString(R.string.logs)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dirPath = intent.getStringExtra("LogMessage")
        dirPath?.let {
            val file = File(dirPath)
            val logs = FileUtils.readFromFile(file)
            mBinding.log.text = logs
        }
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
        intent.putExtra(
            Intent.EXTRA_TEXT,
            mBinding.log.toString()
        )
        val uriForFile =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
        intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}