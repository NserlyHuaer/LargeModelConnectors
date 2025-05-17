package com.nserly.Tools.Connection.MessageCollections;

import com.nserly.Tools.Connection.Sender;

/**
 * 数据包发送处理（java Object -> json）
 */
public abstract class SuperSend {

    public abstract String getJson();

    public static String getJson(SuperSend superSend) {
        return superSend.getJson();
    }

    public static Object getObject(Class<SuperSend> object, String json) {
        return Sender.gson.fromJson(json, object);
    }
}