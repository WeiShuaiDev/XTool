package com.linwei.tool.utils;

import com.linwei.tool.XToolReporter;
import com.linwei.tool.bean.HttpLog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();

        String body = response.body().string();

        HttpLog networkLog = new HttpLog(request.method(), String.valueOf(request.url()), new Date().getTime(),
                String.valueOf(response.headers()), String.valueOf(response.code()), body, (t2 - t1) / 1e6d, "", bodyToString(request));

        MediaType contentType = response.body().contentType();

        ThreadManager.runOnThread(() -> {
            String reportPath = XToolReporter.getNetworkReportPath();
            String log = new Gson().toJson(networkLog);
            SaveUtil.save(reportPath, Constants.NETWORK_REPORT_DIR, Constants.HTTP_SUFFIX,
                    Constants.FILE_EXTENSION, log);
        });

        return response.newBuilder().body(ResponseBody.create(contentType, body)).build();
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "";
        }
    }
}
