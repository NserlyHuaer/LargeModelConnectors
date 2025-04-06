package com.nserly.ConnectionManager.DeepSeek;

import com.nserly.Tools.Connection.Connection;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.CheckBalanceByReceive;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.GetModelByReceive;
import com.nserly.Tools.Connection.Sender;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

import java.io.IOException;
import java.util.ArrayList;

public class Manager {
    private Connection connection;
    @Getter
    private Sender sender;

    @Getter
    @Setter
    private Callback callback;

    public static final String BalanceAddress = "https://api.deepseek.com/user/balance";
    public static final String ModelsAddress = "https://api.deepseek.com/models";

    public static final String ConnectionAddress = "https://api.deepseek.com/v1/chat/completions";

    public static final ArrayList<String> UsualModels;

    static {
        UsualModels = new ArrayList<>();
        UsualModels.add("deepseek-chat");
        UsualModels.add("deepseek-reasoner");
    }


    public Manager(String API_Key, Callback callback) {
        sender = new Sender(ConnectionAddress, API_Key);
        connection = new Connection(callback);
    }


    public ArrayList<GetModelByReceive.ModelDescribe> getSupportModel() throws IOException {
        if (connection == null) {
            return null;
        }

        Request request = Sender.getRequestOnGet(ModelsAddress, new Header("Authorization", "Bearer " + sender.getAPI_Key()));


        try (Response response = connection.sendByCurrentThread(request)) {
            if (!response.isSuccessful()) {
                return null;
            }

            String responseBody = response.body().string();
            GetModelByReceive result = Sender.gson.fromJson(responseBody, GetModelByReceive.class);
            return result.getData();
        }
    }

    public CheckBalanceByReceive.Balance_infos getBalance() throws IOException {
        if (connection == null) return null;
        Request request = Sender.getRequestOnGet(BalanceAddress, new Header("Authorization", "Bearer " + sender.getAPI_Key()));
        try (Response response = connection.sendByCurrentThread(request)) {
            String responseBody = response.body().string();
            CheckBalanceByReceive checkBalanceByReceive = Sender.gson.fromJson(responseBody, CheckBalanceByReceive.class);
            return checkBalanceByReceive.getBalance_infos().getFirst();
        }
    }
}
