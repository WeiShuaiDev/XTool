package com.linwei.tool.ui.network

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.linwei.tool.R
import com.linwei.tool.utils.FileUtils
import java.io.File

class AppLogDetailsActivity : AppCompatActivity() {

    private lateinit var mLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_log_details)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar).apply {
            title = getString(R.string.logs)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mLog = findViewById(R.id.log)

        val dirPath = intent.getStringExtra("LogMessage")
        dirPath?.let {
            val file = File(dirPath)
            val logs = FileUtils.readFromFile(file)
            mLog.text = logs
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
            mLog.toString()
        )
        val uriForFile =
            FileProvider.getUriForFile(this, "$packageName.fileprovider", File(filePath))
        intent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}