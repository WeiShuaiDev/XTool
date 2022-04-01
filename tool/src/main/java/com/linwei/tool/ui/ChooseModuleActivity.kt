package com.linwei.tool.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.linwei.tool.R
import com.linwei.tool.databinding.ActivityChooseModuleBinding
import com.linwei.tool.ui.crash.CrashReporterActivity
import com.linwei.tool.ui.network.NetworkReporterActivity

class ChooseModuleActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityChooseModuleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChooseModuleBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.toolbar.title = getString(R.string.name)
        setSupportActionBar(mBinding.toolbar)

        mBinding.crash.setOnClickListener {
            startActivity(Intent(this, CrashReporterActivity::class.java))
        }

        mBinding.network.setOnClickListener {
            startActivity(Intent(this, NetworkReporterActivity::class.java))
        }

    }
}