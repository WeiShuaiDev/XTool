package com.linwei.tool.ui.network

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.linwei.tool.XToolReporter
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import com.linwei.tool.databinding.FragmentLogBinding
import com.linwei.tool.adapter.NetworkLogAdapter
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.SaveUtil
import java.io.File
import java.lang.RuntimeException
import java.util.*

class HttpLogFragment : Fragment() {

    private lateinit var mHttptLogBinding: FragmentLogBinding
    private lateinit var mContext: Context

    private var mLogAdapter: NetworkLogAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mHttptLogBinding = FragmentLogBinding.inflate(inflater, container, false)
        return mHttptLogBinding.root
    }

    override fun onResume() {
        super.onResume()
        loadAdapter(mContext)
    }

    private fun loadAdapter(context: Context) {
        mLogAdapter = NetworkLogAdapter(context, allNetwork)
        mHttptLogBinding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mLogAdapter
        }

        if (allNetwork.size > 0) {
            mHttptLogBinding.recyclerView.visibility=View.VISIBLE
            mHttptLogBinding.textViewState.visibility=View.GONE
        } else {
            mHttptLogBinding.recyclerView.visibility=View.GONE
            mHttptLogBinding.textViewState.visibility=View.VISIBLE
        }
    }

    fun clearLog() {
        if (allNetwork.size > 0) {
            mLogAdapter?.updateList(allNetwork)
            mHttptLogBinding.recyclerView.visibility=View.VISIBLE
            mHttptLogBinding.textViewState.visibility=View.GONE
        } else {
            mHttptLogBinding.recyclerView.visibility=View.GONE
            mHttptLogBinding.textViewState.visibility=View.VISIBLE
        }
    }

    private val allNetwork: ArrayList<File>
        get() {
            val directoryPath: String
            val networkReportPath = XToolReporter.getNetworkReportPath()
            directoryPath = if (TextUtils.isEmpty(networkReportPath)) {
                SaveUtil.getDefaultPath(Constants.NETWORK_REPORT_DIR)
            } else {
                networkReportPath
            }
            val directory = File(directoryPath)
            if (!directory.exists() || !directory.isDirectory) {
                throw RuntimeException("The path provided doesn't exists : $directoryPath")
            }
            val listOfFiles = arrayListOf(*directory.listFiles())
            val iterator = listOfFiles.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().name.contains(Constants.LOGS_SUFFIX)) {
                    iterator.remove()
                }
            }
            Collections.sort(listOfFiles, Collections.reverseOrder())
            return listOfFiles
        }
}