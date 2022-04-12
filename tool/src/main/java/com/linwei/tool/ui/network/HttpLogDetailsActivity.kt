package com.linwei.tool.ui.network

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.linwei.tool.R
import com.linwei.tool.bean.HttpLog
import com.linwei.tool.utils.FileUtils
import java.io.File
import java.util.*

class HttpLogDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log_details)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar).apply {
            title = getString(R.string.http)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val dirPath = intent.getStringExtra("LogMessage")
        dirPath?.let {
            val file = File(dirPath)
            Gson().fromJson(FileUtils.readFromFile(file), HttpLog::class.java)?.apply {
                findViewById<TextView>(R.id.date).text = FileUtils.dateFormat.format(Date(date))
                findViewById<TextView>(R.id.url).text = "[${requestType}]${url}"
                findViewById<TextView>(R.id.code).text = responseCode
                findViewById<TextView>(R.id.latency).text = String.format("%sms", duration.toInt())
                findViewById<TextView>(R.id.headers).text = headers
                findViewById<TextView>(R.id.postData).text = postData
                findViewById<TextView>(R.id.response).apply {
                    text = responseData
                    textSize = 15f
                }

                if (responseCode == "200") {
                    findViewById<ImageView>(R.id.code_img).setBackgroundColor(Color.parseColor("#0c8918"))
                    findViewById<TextView>(R.id.code).setTextColor(Color.parseColor("#0c8918"))
                } else {
                    findViewById<ImageView>(R.id.code_img).setBackgroundColor(Color.parseColor("#be002f"))
                    findViewById<TextView>(R.id.code).setTextColor(Color.parseColor("#be002f"))
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
        val url: String = findViewById<TextView>(R.id.url).text.toString()
        val code: String = findViewById<TextView>(R.id.code).text.toString()
        val postData: String = findViewById<TextView>(R.id.postData).text.toString()
        val response: String = findViewById<TextView>(R.id.response).text.toString()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "url=${url}; code=${code}; postData=${postData}; body=${response}"
        )
        val uriForFile =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
        intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}