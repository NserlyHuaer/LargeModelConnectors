package com.nserly.Tools.Connection.MessageCollections.DeepSeek;

import com.nserly.Tools.Connection.MessageCollections.SuperReceive;
import com.nserly.Tools.Connection.Sender;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

//GET
//https://api.deepseek.com/user/balance
@Setter
@Getter
public class CheckBalanceByReceive extends SuperReceive<CheckBalanceByReceive> {
    //当前账户是否有余额可供 API 调用
    private boolean is_available;
    private ArrayList<Balance_infos> balance_infos;

    @Override
    public CheckBalanceByReceive getObject(String json) {
        return Sender.gson.fromJson(json, CheckBalanceByReceive.class);
    }


    @Setter
    @Getter
    public static class Balance_infos {
        //货币，人民币或美元
        private String currency;
        //总的可用余额，包括赠金和充值余额
        private String total_balance;

        //未过期的赠金余额
        private String granted_balance;

        //充值余额
        private String topped_up_balance;
    }
}
