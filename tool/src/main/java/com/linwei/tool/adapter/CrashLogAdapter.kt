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
import com.linwei.tool.bean.CrashLog
import com.linwei.tool.bean.HttpLog
import com.linwei.tool.ui.crash.LogMessageActivity
import com.linwei.tool.utils.FileUtils
import java.io.File
import java.util.ArrayList

class CrashLogAdapter(private val context: Context, private var crashFileList: ArrayList<File>) :
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
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val messageLogMsg: TextView = itemView.findViewById(R.id.textViewMsg)
        private val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)

        fun setUpViewHolder(context: Context, file: File) {
            val filePath = file.absolutePath
            val jsonData = FileUtils.readFromFile(file)
            val crashLog = Gson().fromJson(jsonData, CrashLog::class.java)
            textViewTime.text = crashLog.createdAt
            textViewTitle.text =crashLog.message
            messageLogMsg.visibility = View.GONE
            textViewTime.visibility = View.VISIBLE
            itemView.setOnClickListener {
                val intent = Intent(context, LogMessageActivity::class.java)
                intent.putExtra("LogMessage", filePath)
                context.startActivity(intent)
            }
        }

    }
}