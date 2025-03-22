package com.nserly.Tools.Connection.MessageCollections.DeepSeek;

import com.google.gson.Gson;
import com.nserly.Tools.Connection.MessageCollections.SuperSend;
import com.nserly.Tools.Connection.Sender;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

//POST
//https://api.deepseek.com/chat/completions
@Getter
public class ChatBySend extends SuperSend {
    //发送信息内容
    @Setter
    private ArrayList<ChatBySendMessage> messages;
    @Setter
    private String model;
    @Setter
    //介于 -2.0 到 2.0 间
    private double frequency_penalty = 0;
    @Setter
    //介于 1 到 8192 间
    private int max_tokens = 2048;
    @Setter
    //介于 -2.0 和 2.0 之间的数字
    private double presence_penalty;


    @Setter
    //一个 string 或最多包含 16 个 string 的 list，在遇到这些词时，API 将停止生成更多的 token。
    private ArrayList<String> stop;
    @Setter
    //如果设置为 True，将会以 SSE（server-sent events）的形式以流式发送消息增量。消息流以 data: [DONE] 结尾。
    private boolean stream;
    @Setter
    //流式输出相关选项。只有在 stream 参数为 true 时，才可设置此参数。
    private Stream_options stream_options;
    //采样温度，介于 0 和 2 之间。更高的值，如 0.8，会使输出更随机，而更低的值，如 0.2，会使其更加集中和确定。 我们通常建议可以更改这个值或者更改 top_p，但不建议同时对两者进行修改。
    private double temperature;

    public void setTemperature(double temperature) {
        this.temperature = Double.parseDouble(String.format("%.1f", temperature));
    }

    public void setTemperature(String temperature) {
        setTemperature(Double.parseDouble(temperature));
    }

    public void addMessage(ChatBySendMessage... chatBySendMessage) {
        this.messages.addAll(Arrays.asList(chatBySendMessage));
    }

    @Override
    public String getJson() {
        return Sender.gson.toJson(this);
    }


    //发送信息内容
    @Setter
    @Getter
    public static class ChatBySendMessage {
        private String content;
        private String role;

        public ChatBySendMessage(String content, String role) {
            this.content = content;
            this.role = role;
        }

        public ChatBySendMessage() {

        }
    }

    //stream_options
    @Getter
    public static class Stream_options {
        //如果设置为 true，在流式消息最后的 data: [DONE] 之前将会传输一个额外的块。此块上的 usage 字段显示整个请求的 token 使用统计信息，而 choices 字段将始终是一个空数组。所有其他块也将包含一个 usage 字段，但其值为 null。
        private final boolean include_usage;

        public Stream_options(boolean include_usage) {
            this.include_usage = include_usage;
        }

    }
}
