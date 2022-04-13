package com.linwei.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.linwei.tool.ui.ChooseModuleActivity;
import com.linwei.tool.ui.crash.CrashReporterActivity;
import com.linwei.tool.ui.crash.LogMessageActivity;
import com.linwei.tool.ui.network.AppLogDetailsActivity;
import com.linwei.tool.ui.network.HttpLogDetailsActivity;
import com.linwei.tool.ui.network.NetworkReporterActivity;
import com.linwei.tool.utils.CrashReporterNotInitializedException;
import com.linwei.tool.utils.CrashReporterExceptionHandler;
import com.linwei.tool.utils.CrashUtil;
import com.linwei.tool.utils.bubble.BubbleLayout;
import com.linwei.tool.utils.bubble.BubblesManager;

public class XToolReporter {

    private static Context applicationContext;

    private static String crashReportPath;

    private static String networkReportPath;

    private static boolean isNotificationEnabled = true;

    private static boolean isAndzuEnabled;

    private static boolean isAndzuActivated;

    @SuppressLint("StaticFieldLeak")
    public static BubblesManager bubblesManager;

    private static BubbleLayout bubbleView;

    private XToolReporter() {
    }

    public static void init(Context context) {
        applicationContext = context;
        setUpExceptionHandler();
    }

    public static void init(Context context, String crashReportSavePath, String networkReportSavePath) {
        applicationContext = context;
        crashReportPath = crashReportSavePath;
        networkReportPath = networkReportSavePath;
        setUpExceptionHandler();
    }

    private static void setUpExceptionHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashReporterExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashReporterExceptionHandler());
        }
    }

    public static void initBubbles(Application context) {
        if (!isAndzuEnabled) {
            context.registerActivityLifecycleCallbacks(lifecycleCallbacks);
            bubblesManager = new BubblesManager.Builder(context)
                    .setInitializationCallback(() -> {
                        isAndzuEnabled = true;
                        addNewBubble(context);
                    })
                    .build();
            bubblesManager.initialize();
        }else{
            enableAndzu();
        }
    }

    public static void recycle() {
        if (bubblesManager != null) {
            bubblesManager.recycle();
        }
    }

    private static void addNewBubble(Context context) {
        bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.bubble_layout, null);
        bubbleView.setOnBubbleClickListener(bubble -> {
            if (!isAndzuActivated) {
                isAndzuActivated = true;
                Intent intent = new Intent(context, ChooseModuleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        bubbleView.setShouldStickToWall(false);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            enableAndzu();
            if (activity instanceof ChooseModuleActivity) {
                isAndzuActivated = true;
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            enableAndzu();
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity instanceof ChooseModuleActivity) {
                isAndzuActivated = false;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

    public static  void enableAndzu() {
        if (isAndzuEnabled && bubbleView != null) {
            bubbleView.setVisibility(View.VISIBLE);
        }
    }

    public static  void disableAndzu() {
        if (isAndzuEnabled && bubbleView != null) {
            bubbleView.setVisibility(View.GONE);
        }
    }

    public static Context getContext() {
        if (applicationContext == null) {
            try {
                throw new CrashReporterNotInitializedException("Initialize CrashReporter : call CrashReporter.initialize(context, crashReportPath)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applicationContext;
    }

    public static String getCrashReportPath() {
        return crashReportPath;
    }

    public static String getNetworkReportPath() {
        return networkReportPath;
    }

    public static boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public static void disableNotification() {
        isNotificationEnabled = false;
    }

    public static void enableNotification() {
        isNotificationEnabled = true;
    }


    public static void logException(Exception exception) {
        CrashUtil.logException(exception);
    }

    public static Intent getLaunchCrashIntent() {
        return new Intent(applicationContext, CrashReporterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getLaunchNetworkIntent() {
        return new Intent(applicationContext, NetworkReporterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

}
