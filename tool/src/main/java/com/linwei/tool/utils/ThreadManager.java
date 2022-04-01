package com.linwei.tool.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 */
@SuppressWarnings("all")
public class ThreadManager {

    private static Handler sMainHandler = new Handler(Looper.getMainLooper());


    public static ThreadPool instance;

    /**
     * 获取单例的线程池对象
     *
     * @return ...
     */
    public static ThreadPool getThreadPool() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    // 获取处理器数量
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    // 根据cpu数量,计算出合理的线程并发数
                    int threadNum = cpuNum * 2 + 1;
                    instance = new ThreadPool(threadNum - 1, threadNum, Integer.MAX_VALUE);
                }
            }
        }
        return instance;
    }

    public static class ThreadPool {
        private ThreadPoolExecutor mExecutor;
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            if (mExecutor == null) {
                ThreadFactory threadFactory = Executors.defaultThreadFactory();
                mExecutor = new ThreadPoolExecutor(
                        // 核心线程数
                        corePoolSize,
                        // 最大线程数
                        maximumPoolSize,
                        // 闲置线程存活时间
                        keepAliveTime,
                        // 时间单位
                        TimeUnit.MILLISECONDS,
                        // 线程队列
                        new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE),
                        // 线程工厂
                        threadFactory,
                        // 队列已满,而且当前线程数已经超过最大线程数时的异常处理策略
                        new ThreadPoolExecutor.AbortPolicy() {
                            @Override
                            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                super.rejectedExecution(r, e);
                            }
                        });
            }
            mExecutor.execute(runnable);
        }

        /**
         * 计数器，为0时就会执行主线程逻辑
         */
        public void shutdown() {
            if (mExecutor != null) {
                mExecutor.shutdown();
            }
        }

        /**
         * 从线程队列中移除任务对象
         */
        public void cancel(Runnable runnable) {
            if (mExecutor != null) {
                mExecutor.getQueue().remove(runnable);
            }
        }
    }

    public static void runOnThread(Runnable runnable) {
        getThreadPool().execute(runnable);
    }

    /**
     * UI线程执行
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        sMainHandler.post(runnable);
    }

    public static void postDelayedOnUiThread(Runnable runnable, long delayMillis) {
        sMainHandler.postDelayed(runnable, delayMillis);
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取当前线程名称
     */
    public static String getCurrentThreadName() {
        String result = "UNKOWN";
        Looper looper = Looper.myLooper();
        if (looper != null) {
            result = looper.getThread().getName();
        }
        return result;
    }

    public static Handler getsMainHandler() {
        return sMainHandler;
    }
}