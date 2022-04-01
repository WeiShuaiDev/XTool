package com.linwei.tool.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.content.Intent
import android.view.View
import com.linwei.tool.R
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
        private val textViewTitle: TextView = itemView.findViewById<View>(R.id.textViewTitle) as TextView
        private val messageLogMsg: TextView = itemView.findViewById<View>(R.id.textViewMsg) as TextView
        private val textViewTime: TextView = itemView.findViewById<View>(R.id.textViewTime) as TextView


        fun setUpViewHolder(context: Context, file: File) {
            val filePath = file.absolutePath
            textViewTime.text = file.name.replace("[a-zA-Z_.]".toRegex(), "")
            textViewTitle.text = FileUtils.readFirstLineFromFile(File(filePath))
            messageLogMsg.visibility=View.GONE
            textViewTime.visibility=View.VISIBLE
            itemView.setOnClickListener {
                val intent = Intent(context, LogMessageActivity::class.java)
                intent.putExtra("LogMessage", filePath)
                context.startActivity(intent)
            }
        }

    }
}