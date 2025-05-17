package com.nserly.Tools.Connection.MessageCollections.DeepSeek.FunctionCalling;

import com.nserly.Tools.Connection.MessageCollections.SuperSend;
import lombok.Getter;

import java.util.List;

/**
 * 定义函数调用基本格式
 */
public abstract class SuperFunctionCallingDefinition extends SuperSend {
    @Getter
    public static String name;
    @Getter
    public static String description;
    @Getter
    public static Parameters parameters;

    public SuperFunctionCallingDefinition() {

    }

    public static abstract class Parameters {
        @Getter
        public static String type;
        @Getter
        public static List<String> required;
        @Getter
        public static Properties properties;

        public static abstract class Properties {
            @Getter
            public static Location location;

            public static abstract class Location {
                public static String type;
                public static String description;
            }
        }
    }
}
