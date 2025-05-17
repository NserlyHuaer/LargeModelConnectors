package com.nserly.Tools.Connection.MessageCollections.DeepSeek.FunctionCalling;

import lombok.Getter;
import lombok.Setter;

public class WrappingFunctionCalling {
    @Getter
    private static final String type = "function";
    @Getter
    @Setter
    private SuperFunctionCallingDefinition function;

    public WrappingFunctionCalling(SuperFunctionCallingDefinition superFunctionCallingDefinition) {
        this.function = superFunctionCallingDefinition;
    }

}
