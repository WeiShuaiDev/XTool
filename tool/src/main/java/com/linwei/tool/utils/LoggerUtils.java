package com.linwei.tool.utils;

import com.linwei.tool.XToolReporter;
import com.linwei.tool.bean.AppLog;
import com.google.gson.Gson;

import java.util.Date;

public class LoggerUtils {

    private static final String LOGGER = "Logger";

    private static void save(String logType, String msg) {
        String createAt = SaveUtil.getLogTime();
        AppLog appLog = new AppLog(LOGGER, logType, createAt, msg);
        ThreadManager.runOnThread(() -> {
            String reportPath = XToolReporter.getNetworkReportPath();
            String log = new Gson().toJson(appLog);
            SaveUtil.save(reportPath, Constants.NETWORK_REPORT_DIR, Constants.LOGS_SUFFIX,
                    Constants.FILE_EXTENSION, log);
        });
    }

    public static void i(String msg) {
        save("info", msg);
    }

    public static void e(String msg) {
        save("error", msg);
    }

    public static void d(String msg) {
        save("debug", msg);
    }

    public static void v(String msg) {
        save("verbose", msg);
    }

    public static void w(String msg) {
        save("warn", msg);
    }

}
