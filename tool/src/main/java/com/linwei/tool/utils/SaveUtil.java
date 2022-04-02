package com.linwei.tool.utils;

import android.text.TextUtils;
import android.util.Log;

import com.linwei.tool.XToolReporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveUtil {

    private static final String TAG = CrashUtil.class.getSimpleName();

    public static String getLogTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private static final SnowFlake showFlake = new SnowFlake(2, 3);

    private static String getFileName(String suffix, String extension) {
        long random = showFlake.nextId();
        return random + suffix + extension;
    }

    /**
     * 保存数据
     *
     * @param reportPath String
     * @param dir        String
     * @param suffix     String
     * @param log        String
     */
    public static void save(String reportPath, String dir, String suffix, String extension, String log) {

        String fileName = getFileName(suffix, extension);

        if (TextUtils.isEmpty(reportPath)) {
            reportPath = getDefaultPath(dir);
        }

        File crashDir = new File(reportPath);
        if (!crashDir.exists() || !crashDir.isDirectory()) {
            reportPath = getDefaultPath(dir);
            Log.e(TAG, "Path provided doesn't exists : " + crashDir + "\nSaving crash report at : " + getDefaultPath(dir));
        }

        writeToFile(reportPath, fileName, log);
    }

    private static void writeToFile(String reportPath, String filename, String log) {

        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(
                    reportPath + File.separator + filename));

            bufferedWriter.write(log);
            bufferedWriter.flush();
            bufferedWriter.close();
            Log.d(TAG, "crash report saved in : " + reportPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDefaultPath(String dir) {
        String defaultPath = XToolReporter.getContext().getExternalFilesDir(null).getAbsolutePath()
                + File.separator + dir;

        File file = new File(defaultPath);
        file.mkdirs();
        return defaultPath;
    }
}
