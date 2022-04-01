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
import com.linwei.tool.bean.HttpLog
import com.linwei.tool.databinding.ActivityHttpLogDetailsBinding
import com.linwei.tool.utils.FileUtils
import java.io.File
import java.util.*

class HttpLogDetailsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityHttpLogDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHttpLogDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.title = getString(R.string.http)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dirPath = intent.getStringExtra("LogMessage")
        dirPath?.let {
            val file = File(dirPath)
            Gson().fromJson(FileUtils.readFromFile(file), HttpLog::class.java)?.apply {
                mBinding.date.text = FileUtils.dateFormat.format(Date(date))
                mBinding.url.text = "[${requestType}]${url}"
                mBinding.code.text = responseCode
                mBinding.latency.text = String.format("%sms", duration.toInt())
                mBinding.headers.text = headers
                mBinding.postData.text = postData
                mBinding.response.text = responseData
                mBinding.response.textSize = 15f

                if (responseCode=="200") {
                    mBinding.codeImg.setBackgroundColor(Color.parseColor("#0c8918"))
                    mBinding.code.setTextColor(Color.parseColor("#0c8918"))
                } else {
                    mBinding.codeImg.setBackgroundColor(Color.parseColor("#be002f"))
                    mBinding.code.setTextColor(Color.parseColor("#be002f"))
                }
            }
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
            "url=${mBinding.url}; code=${mBinding.code}; postData=${mBinding.postData}; body=${mBinding.response}"
        )
        val uriForFile =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
        intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}