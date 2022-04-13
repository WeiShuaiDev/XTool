package com.linwei.tool.ui.network

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.linwei.tool.R
import com.linwei.tool.bean.HttpLog
import com.linwei.tool.utils.FileUtils
import com.linwei.tool.view.JsonRecyclerView
import java.io.File
import java.util.*

class HttpLogDetailsActivity : AppCompatActivity() {

    private var mHttpLog: HttpLog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log_details)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar).apply {
            title = getString(R.string.http)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<JsonRecyclerView>(R.id.recyclerView)
        val scrollView = findViewById<HorizontalScrollView>(R.id.scrollView)
        recyclerView.setScaleEnable(true)
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.getAction() and e.getActionMasked()) {
                    MotionEvent.ACTION_DOWN -> {
                    }
                    MotionEvent.ACTION_UP -> {
                    }
                    MotionEvent.ACTION_POINTER_UP -> scrollView.requestDisallowInterceptTouchEvent(
                        false
                    )
                    MotionEvent.ACTION_POINTER_DOWN -> scrollView.requestDisallowInterceptTouchEvent(
                        true
                    )
                    MotionEvent.ACTION_MOVE -> {
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })


        val dirPath = intent.getStringExtra("LogMessage")
        dirPath?.let {
            val file = File(dirPath)
            mHttpLog = Gson().fromJson(FileUtils.readFromFile(file), HttpLog::class.java)?.apply {
                findViewById<TextView>(R.id.date).text = FileUtils.dateFormat.format(Date(date))
                findViewById<TextView>(R.id.url).text = "[${requestType}]${url}"
                findViewById<TextView>(R.id.code).text = responseCode
                findViewById<TextView>(R.id.latency).text = String.format("%sms", duration.toInt())
                findViewById<TextView>(R.id.headers).text = headers
                findViewById<TextView>(R.id.postData).text = postData
                findViewById<JsonRecyclerView>(R.id.recyclerView).bindJson(responseData)

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
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "url=${mHttpLog?.url}; code=${mHttpLog?.responseCode}; postData=${mHttpLog?.postData}; body=${mHttpLog?.responseData}"
        )
        val uriForFile =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
        intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}