package com.linwei.xtool

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.linwei.tool.XToolReporter
import com.linwei.tool.ui.crash.CrashReporterActivity
import com.linwei.tool.ui.network.NetworkReporterActivity
import com.linwei.tool.utils.LoggerUtils
import com.linwei.tool.utils.LoggingInterceptor
import com.linwei.tool.utils.ThreadManager
import com.linwei.xtool.databinding.ActivityMainBinding
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    var context: Context? = null
    var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.divideByZero.setOnClickListener { v: View? ->
            val num = 1 / 0
        }
        mBinding.indexOutOfBound.setOnClickListener { v: View? ->
            val list = mutableListOf<String>()
            list.add("hello")
            val crashMe = list[4]
        }
        mBinding.classCastExeption.setOnClickListener { v: View? ->
            val x: Any = 0
            println(x as String)
        }

        //Crashes and exceptions are also captured from other threads
        Thread {
            try {
                context = null
                context!!.resources
            } catch (e: Exception) {
                //log caught Exception
                XToolReporter.logException(e)
            }
        }.start()

        mContext = this
        mBinding.crashLogActivity.setOnClickListener { v: View? ->
            val intent = Intent(mContext, CrashReporterActivity::class.java)
            startActivity(intent)
        }

        mBinding.networkError.setOnClickListener {

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor())
                .build()
            ThreadManager.runOnThread {
                val request: okhttp3.Request = okhttp3.Request.Builder()
                    .url(
                        "https://www.wanandroid.com/article/list/0/json"
                    )
                    .build()

                client.newCall(request).execute()
            }
        }

        mBinding.appLog.setOnClickListener {
            LoggerUtils.i("info")

            LoggerUtils.e("error")

            LoggerUtils.d("debug")

            LoggerUtils.v("verbose")

            LoggerUtils.w("warn")
        }

        mBinding.networkLogActivity.setOnClickListener {
            val intent = Intent(mContext, NetworkReporterActivity::class.java)
            startActivity(intent)
        }
        mBinding.rubbles.setOnClickListener {
            XToolReporter.disableAndzu()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1)
            } else {
                XToolReporter.initBubbles(SampleApplication.appContext)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                XToolReporter.initBubbles(SampleApplication.appContext)
            } else { //Permission is not available
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            XToolReporter.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}