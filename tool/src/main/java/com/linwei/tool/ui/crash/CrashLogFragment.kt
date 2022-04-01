package com.linwei.tool.ui.crash

import android.content.Context
import com.linwei.tool.adapter.CrashLogAdapter
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.linwei.tool.XToolReporter
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import com.linwei.tool.adapter.NetworkLogAdapter
import com.linwei.tool.databinding.FragmentLogBinding
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.SaveUtil
import java.io.File
import java.lang.RuntimeException
import java.util.*

class CrashLogFragment : Fragment() {

    private lateinit var mCrashLogBinding: FragmentLogBinding
    private lateinit var mContext: Context

    private var mLogAdapter: CrashLogAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mCrashLogBinding = FragmentLogBinding.inflate(inflater, container, false)
        return mCrashLogBinding.root
    }

    override fun onResume() {
        super.onResume()
        loadAdapter(mContext)
    }

    private fun loadAdapter(context: Context) {
        mLogAdapter = CrashLogAdapter(context, allCrashes)
        mCrashLogBinding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mLogAdapter
        }
        if (allCrashes.size > 0) {
            mCrashLogBinding.recyclerView.visibility=View.VISIBLE
            mCrashLogBinding.textViewState.visibility=View.GONE
        } else {
            mCrashLogBinding.recyclerView.visibility=View.GONE
            mCrashLogBinding.textViewState.visibility=View.VISIBLE
        }
    }

    fun clearLog() {
        if (allCrashes.size > 0) {
            mLogAdapter?.updateList(allCrashes)
            mCrashLogBinding.recyclerView.visibility=View.VISIBLE
            mCrashLogBinding.textViewState.visibility=View.GONE
        } else {
            mCrashLogBinding.recyclerView.visibility=View.GONE
            mCrashLogBinding.textViewState.visibility=View.VISIBLE
        }
    }

    private val allCrashes: ArrayList<File>
        get() {
            val directoryPath: String
            val crashReportPath = XToolReporter.getCrashReportPath()
            directoryPath = if (TextUtils.isEmpty(crashReportPath)) {
                SaveUtil.getDefaultPath(Constants.CRASH_REPORT_DIR)
            } else {
                crashReportPath
            }
            val directory = File(directoryPath)
            if (!directory.exists() || !directory.isDirectory) {
                throw RuntimeException("The path provided doesn't exists : $directoryPath")
            }
            val listOfFiles = arrayListOf(*directory.listFiles())
            val iterator = listOfFiles.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().name.contains(Constants.EXCEPTION_SUFFIX)) {
                    iterator.remove()
                }
            }
            Collections.sort(listOfFiles, Collections.reverseOrder())
            return listOfFiles
        }
}