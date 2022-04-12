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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.linwei.tool.R
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.SaveUtil
import java.io.File
import java.lang.RuntimeException
import java.util.*

class ExceptionLogFragment : Fragment() {
    private lateinit var mContext: Context

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mTextViewState: TextView

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
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mTextViewState = view.findViewById(R.id.textViewState)
    }

    override fun onResume() {
        super.onResume()
        loadAdapter(mContext, mRecyclerView)
    }

    private fun loadAdapter(context: Context, exceptionRecyclerView: RecyclerView) {
        mLogAdapter = CrashLogAdapter(context, allExceptions)
        mRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mLogAdapter
        }
        if (allExceptions.size > 0) {
            mRecyclerView.visibility=View.VISIBLE
            mTextViewState.visibility=View.GONE
        } else {
            mRecyclerView.visibility=View.GONE
            mTextViewState.visibility=View.VISIBLE
        }
    }

    fun clearLog() {
        if (allExceptions.size > 0) {
            mLogAdapter?.updateList(allExceptions)
            mRecyclerView.visibility=View.VISIBLE
            mTextViewState.visibility=View.GONE
        } else {
            mRecyclerView.visibility=View.GONE
            mTextViewState.visibility=View.VISIBLE
        }
    }

    private val allExceptions: ArrayList<File>
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
                if (iterator.next().name.contains(Constants.CRASH_SUFFIX)) {
                    iterator.remove()
                }
            }
            Collections.sort(listOfFiles, Collections.reverseOrder())
            return listOfFiles
        }
}