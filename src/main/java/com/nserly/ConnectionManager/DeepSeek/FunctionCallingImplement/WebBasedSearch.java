package com.nserly.ConnectionManager.DeepSeek.FunctionCallingImplement;

import com.nserly.Tools.Connection.MessageCollections.DeepSeek.FunctionCalling.SuperFunctionCallingDefinition;
import com.nserly.Tools.Connection.Sender;

public class WebBasedSearch extends SuperFunctionCallingDefinition {

    @Override
    public String getJson() {
        return Sender.gson.toJson(this);
    }
}
