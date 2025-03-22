package com.nserly.Tools.Connection.MessageCollections;

import com.nserly.Tools.Connection.Sender;

/**
 * 接收数据包处理（json -> java Object）
 *
 * @param <Son> 子类
 */

public abstract class SuperReceive<Son> {

    public abstract Son getObject(String json);

    public static String getJson(SuperReceive<?> object) {
        return Sender.gson.toJson(object);
    }

    public static Object getObject(Class<SuperReceive<?>> object, String json) {
        return Sender.gson.fromJson(json, object);
    }
}
