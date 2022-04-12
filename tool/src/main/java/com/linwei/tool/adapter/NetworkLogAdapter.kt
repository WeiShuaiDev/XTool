package com.linwei.tool.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.content.Intent
import android.view.View
import com.google.gson.Gson
import com.linwei.tool.R
import com.linwei.tool.bean.AppLog
import com.linwei.tool.bean.HttpLog
import com.linwei.tool.ui.network.AppLogDetailsActivity
import com.linwei.tool.ui.network.HttpLogDetailsActivity
import com.linwei.tool.utils.Constants
import com.linwei.tool.utils.FileUtils
import java.io.File
import java.util.ArrayList

class NetworkLogAdapter(private val context: Context, private var crashFileList: ArrayList<File>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_item, null)
        return CrashLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CrashLogViewHolder).setUpViewHolder(context, crashFileList[position])
    }

    override fun getItemCount(): Int {
        return crashFileList.size
    }

    fun updateList(allCrashLogs: ArrayList<File>) {
        crashFileList = allCrashLogs
        notifyDataSetChanged()
    }

    private class CrashLogViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val state: View =
            itemView.findViewById(R.id.state)
        private val textViewMsg: TextView =
            itemView.findViewById(R.id.textViewMsg)
        private val textViewTime: TextView =
            itemView.findViewById(R.id.textViewTime)
        private val textViewTitle: TextView =
            itemView.findViewById(R.id.textViewTitle)
        private val textViewTag: TextView =
            itemView.findViewById(R.id.textViewTag)

        fun setUpViewHolder(context: Context, file: File) {
            val filePath = file.absolutePath
            val jsonData = FileUtils.readFromFile(file)

            if (filePath.contains(Constants.HTTP_SUFFIX)) {
                val httpLog = Gson().fromJson(jsonData, HttpLog::class.java)
                textViewTitle.text = httpLog.url

                textViewTime.text = file.name.replace("[a-zA-Z_.]".toRegex(), "")
                textViewTime.visibility = View.VISIBLE

                textViewTag.text = httpLog.requestType

                textViewTag.visibility = View.VISIBLE
                textViewMsg.text = httpLog.postData

                state.visibility = View.VISIBLE
                if ("200" == httpLog.responseCode) {
                    state.setBackgroundResource(R.color.c_0c8918)
                } else {
                    state.setBackgroundResource(R.color.c_be002f)
                }

            } else {
                val appLog = Gson().fromJson(jsonData, AppLog::class.java)
                textViewTime.text = appLog.createdAt
                textViewTitle.text = FileUtils.readFirstLineFromFile(File(filePath))
                textViewTag.text=appLog.logType

                textViewMsg.visibility = View.GONE
                textViewTag.visibility = View.VISIBLE
                textViewTime.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                if (filePath.contains(Constants.HTTP_SUFFIX)) {
                    val intent = Intent(context, HttpLogDetailsActivity::class.java)
                    intent.putExtra("LogMessage", filePath)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, AppLogDetailsActivity::class.java)
                    intent.putExtra("LogMessage", filePath)
                    context.startActivity(intent)

                }
            }
        }

    }
}