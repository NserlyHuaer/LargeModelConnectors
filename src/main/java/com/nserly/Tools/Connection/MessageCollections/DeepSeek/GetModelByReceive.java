package com.nserly.Tools.Connection.MessageCollections.DeepSeek;

import com.nserly.Tools.Connection.MessageCollections.SuperReceive;
import com.nserly.Tools.Connection.Sender;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
//GET
//https://api.deepseek.com/models

@Setter
@Getter
public class GetModelByReceive extends SuperReceive<GetModelByReceive> {
    //一般为list
    private String object;

    //模型内容
    private ArrayList<ModelDescribe> data;

    @Override
    public GetModelByReceive getObject(String json) {
        return Sender.gson.fromJson(json, GetModelByReceive.class);
    }


    @Setter
    @Getter
    public static class ModelDescribe {
        //名称id（如deepseek-chat）
        private String id;
        //类型（如model）
        private String object;
        //拥有该模型的组织（如deepseek）
        private String owned_by;
    }
}
