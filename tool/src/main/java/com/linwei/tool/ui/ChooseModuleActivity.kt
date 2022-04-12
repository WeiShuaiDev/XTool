package com.linwei.tool.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.linwei.tool.R
import com.linwei.tool.ui.crash.CrashReporterActivity
import com.linwei.tool.ui.network.NetworkReporterActivity

class ChooseModuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_module)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar).apply {
            title = getString(R.string.name)
        })

        findViewById<Button>(R.id.crash).setOnClickListener {
            startActivity(Intent(this, CrashReporterActivity::class.java))
        }

        findViewById<Button>(R.id.network).setOnClickListener {
            startActivity(Intent(this, NetworkReporterActivity::class.java))
        }

    }
}