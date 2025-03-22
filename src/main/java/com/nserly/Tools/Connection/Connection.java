package com.nserly.Tools.Connection;

import lombok.Getter;
import lombok.Setter;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 最底层实现：连接大模型服务器
 * 获取Request可以通过Sender类
 */

public class Connection {

    @Getter
    private OkHttpClient client;

    @Setter
    @Getter
    private long connectTimeout = 30;
    @Setter
    @Getter
    private long readTimeout = 0;
    @Setter
    @Getter
    private long writeTimeout = 30;

    @Setter
    @Getter
    private Callback callback;

    /**
     * 实例化
     * @param callback 若使用新线程处理请求，则需要此callback
     */

    public Connection(Callback callback) {
        this.callback = callback;
        client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS) // 流式请求需禁用读超时
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 通过当前线程处理请求
     * @param request 请求包
     * @return 响应
     * @throws IOException 错误
     */
    public Response sendByCurrentThread(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    /**
     * 通过新线程处理请求
     * @param request 请求包
     * @throws IOException 错误
     */
    public void sendByNewThread(Request request) throws IOException {
        client.newCall(request).enqueue(callback);
    }

    /**
     * 通过新线程处理请求
     * @param request 请求包
     * @param callback 处理请求
     * @throws IOException 错误
     */
    public void sendByNewThread(Request request, Callback callback) throws IOException {
        client.newCall(request).enqueue(callback);
    }

}