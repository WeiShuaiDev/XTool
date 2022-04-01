package com.linwei.tool.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;

import com.linwei.tool.R;
import com.linwei.tool.XToolReporter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CrashUtil {

    private CrashUtil() {
        //this class is not publicly instantiable
    }

    public static void saveCrashReport(final Throwable throwable) {
        String message = throwable.getLocalizedMessage();
        if (!TextUtils.isEmpty(message)) {
            String reportPath = XToolReporter.getCrashReportPath();
            SaveUtil.save(reportPath, Constants.CRASH_REPORT_DIR, Constants.CRASH_SUFFIX,
                    Constants.FILE_EXTENSION, throwable.getLocalizedMessage());
            showNotification(throwable.getLocalizedMessage(), true);
        }
    }

    public static void logException(final Exception exception) {
        String message = exception.getLocalizedMessage();
        if (!TextUtils.isEmpty(message)) {
            ThreadManager.runOnThread(() -> {
                String reportPath = XToolReporter.getCrashReportPath();
                SaveUtil.save(reportPath, Constants.CRASH_REPORT_DIR, Constants.EXCEPTION_SUFFIX,
                        Constants.FILE_EXTENSION, getStackTrace(exception));
                showNotification(exception.getLocalizedMessage(), false);
            });
        }
    }

    private static void showNotification(String localisedMsg, boolean isCrash) {

        if (XToolReporter.isNotificationEnabled()) {
            Context context = XToolReporter.getContext();
            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(NOTIFICATION_SERVICE);
            createNotificationChannel(notificationManager, context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_NOTIFICATION_ID);
            builder.setSmallIcon(R.drawable.ic_warning_black_24dp);

            Intent intent = XToolReporter.getLaunchCrashIntent();
            intent.putExtra(Constants.LANDING, isCrash);
            intent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentIntent(pendingIntent);

            builder.setContentTitle(context.getString(R.string.view_crash_report));

            if (TextUtils.isEmpty(localisedMsg)) {
                builder.setContentText(context.getString(R.string.check_your_message_here));
            } else {
                builder.setContentText(localisedMsg);
            }

            builder.setAutoCancel(true);
            builder.setColor(ContextCompat.getColor(context, R.color.colorAccent));

            notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
        }
    }

    private static void createNotificationChannel(NotificationManager notificationManager, Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = context.getString(R.string.notification_crash_report_title);
            String description = "";
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_NOTIFICATION_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static String getStackTrace(Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        e.printStackTrace(printWriter);
        String crashLog = result.toString();
        printWriter.close();
        return crashLog;
    }
}
