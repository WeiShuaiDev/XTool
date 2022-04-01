package com.linwei.tool.bean

import java.util.*

/**
 * 请求数据
 */
data class HttpLog(
    val requestType: String,
    val url: String,
    val date: Long,
    val headers: String,
    val responseCode: String,
    val responseData: String,
    val duration: Double,
    val errorClientDesc: String,
    val postData:String
) {
    override fun toString(): String {
        return String.format(
            Locale.getDefault(),
            """
            Request Type : %s 
            Request Url : %s
            Request Date : %d
            Request Headers : %s
            Response Code : %s
            Response Data : %s
            Duration : %d
            Error Client Desc : %s
            Post Data : %s
            """.trimIndent(), requestType, url, date, headers, responseCode, responseData,
            duration.toLong(), errorClientDesc, postData
        )
    }
}