package com.linwei.tool.bean

/**
 * Log数据
 */
data class AppLog(
    val tag: String,
    val logType: String,
    val createdAt: String,
    val message: String
)