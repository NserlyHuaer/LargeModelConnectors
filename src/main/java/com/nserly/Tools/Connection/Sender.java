package com.nserly.Tools.Connection;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nserly.Tools.Connection.MessageCollections.SuperSend;
import lombok.Getter;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http2.Header;

/**
 * 获取请求包类
 */
public class Sender {
    @Getter
    @Setter
    //服务器地址
    private String ServerAddress;

    @Getter
    @Setter
    //请求api key
    private String API_Key;


    public static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY) // 字段名直接匹配 JSON key
            .enableComplexMapKeySerialization()
            .create();


    /**
     * 初始化
     *
     * @param ServerAddress 服务器地址
     * @param API_Key       服务器api key
     */
    public Sender(String ServerAddress, String API_Key) {
        this.ServerAddress = ServerAddress;
        this.API_Key = API_Key;
    }

    /**
     * 获取请求包
     *
     * @param superSend 发送信息
     * @return 请求包
     */
    public Request getRequest(SuperSend superSend) {
        return getRequest(this.ServerAddress, superSend);
    }

    /**
     * 获取请求包
     *
     * @param ServerAddress 服务器地址
     * @param superSend     发送信息
     * @return 请求包
     */

    public Request getRequest(String ServerAddress, SuperSend superSend) {
        return getRequest(ServerAddress, this.API_Key, superSend);
    }

    /**
     * 获取请求包
     *
     * @param ServerAddress 服务器地址
     * @param API_Key       服务器api key
     * @param superSend     发送信息
     * @return 请求包
     */

    public Request getRequest(String ServerAddress, String API_Key, SuperSend superSend) {
        return getRequestOnPost(ServerAddress, RequestBody.create(superSend.getJson(), MediaType.get("application/json")),
                new Header("Authorization", "Bearer " + API_Key),
                // 声明接受SSE流
                new Header("Accept", "text/event-stream")
        );
//        return new Request.Builder()
//                .url(ServerAddress)
//                .post(RequestBody.create(superSend.getJson(), MediaType.get("application/json")))
//                .addHeader("Authorization", "Bearer " + API_Key)
//                .addHeader("Accept", "text/event-stream") // 声明接受SSE流
//                .build();
    }

    /**
     * 完整的基本请求数据包（请求类型：Post）
     * @param ServerAddress 服务器地址
     * @param requestBody 请求主体
     * @param headers 头项
     * @return 请求包
     */
    public static Request getRequestOnPost(String ServerAddress, RequestBody requestBody, Header... headers) {
        Request.Builder builder = new Request.Builder()
                .url(ServerAddress)
                .post(requestBody);
        for (Header header : headers) {
            if (header == null) continue;
            builder.addHeader(header.name.utf8(), header.value.utf8());
        }
        return builder.build();
    }

    /**
     * 完整的基本请求数据包
     * @param ServerAddress 服务器地址
     * @param headers 头项
     * @return 数据包
     */

    public static Request getRequestOnGet(String ServerAddress, Header... headers) {
        Request.Builder builder = new Request.Builder()
                .url(ServerAddress)
                .get();
        for (Header header : headers) {
            if (header == null) continue;
            builder.addHeader(header.name.utf8(), header.value.utf8());
        }
        return builder.build();
    }
}
