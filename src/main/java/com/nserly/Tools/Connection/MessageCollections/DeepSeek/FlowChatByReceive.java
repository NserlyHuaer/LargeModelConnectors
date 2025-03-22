package com.nserly.Tools.Connection.MessageCollections.DeepSeek;

import com.google.gson.annotations.SerializedName;
import com.nserly.Tools.Connection.MessageCollections.SuperReceive;
import com.nserly.Tools.Connection.Sender;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

//POST
//https://api.deepseek.com/chat/completions
@Setter
@Getter
public class FlowChatByReceive extends SuperReceive<FlowChatByReceive> {

    @SerializedName("id")
    private String id;

    private String object;

    private long created;

    private String model;

    private String system_fingerprint;


    private ArrayList<Choice> choices;

    private Usage usage;

    @Override
    public FlowChatByReceive getObject(String json) {
        return Sender.gson.fromJson(json, FlowChatByReceive.class);
    }

    @Setter
    @Getter
    public static class Choice {

        private int index;

        //此项包含content和reasoning_content
        private Delta delta;

        private String logprobs;
        //存在两种：如果完成则为stop，否则为null

        private String finish_reason;
        //包括：
        // prompt_tokens completion_tokens        total_tokens
        // prompt_tokens_details                  completion_tokens_details
        // prompt_cache_hit_tokens                prompt_cache_miss_tokens
    }

    public static class Delta {

        @Getter
        @Setter
        private String content;

        @Getter
        @Setter
        private String reasoning_content;
    }

    public static class Usage {
        @Getter
        @Setter
        private int prompt_tokens;

        @Getter
        @Setter
        private int completion_tokens;

        @Getter
        @Setter
        private int total_tokens;

        @Getter
        @Setter
        private Prompt_tokens_details prompt_tokens_details;

        @Getter
        @Setter
        private int prompt_cache_hit_tokens;

        @Setter
        @Getter
        private int prompt_cache_miss_tokens;
    }

    @Getter
    public static class Prompt_tokens_details {
        @Setter
        private int cached_tokens;
    }

}
