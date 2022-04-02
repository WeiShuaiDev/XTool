package com.linwei.tool.bean

/**
 * Crash数据
 */
data class CrashLog(
    val tag: String,
    val createdAt: String,
    val message: String?
)