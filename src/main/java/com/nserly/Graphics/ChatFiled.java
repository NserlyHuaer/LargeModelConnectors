package com.nserly.Graphics;

import com.google.gson.JsonSyntaxException;
import com.nserly.Controller.ChatController;
import com.nserly.Logger;
import com.nserly.MainJavaFXRunner;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatBySend;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.FlowChatByReceive;
import com.nserly.Tools.Connection.Sender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okio.BufferedSource;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class ChatFiled extends JTextPane {
    private transient StringBuffer currentGenerateContent;
    @Getter
    private final ArrayList<ChatBySend.ChatBySendMessage> messages;
    private transient boolean isThink;
    private transient long startToThinkTime;
    private StyledDocument doc;

    private transient int Font_Styles;
    //正常
    private static final int Font_Normal = 0;
    //斜体
    private static final int Font_italic = 1;
    //粗体
    private static final int Font_Bold = 2;
    //粗斜体
    private static final int Font_BoldItalics = 3;
    //下划线
    private static final int Font_UnderLine = 4;
    private transient int Font_Size;
    private static final int FirstTitle = 8;
    private static final int SecondTitle = 6;
    private static final int ThirdTitle = 4;
    private static final int FourthTitle = 3;
    private static final int FifthTitle = 2;
    private static final int SixthTitle = 1;

    @Getter
    private long spentTotalTokensInThisObject;
    @Setter
    @Getter
    private static long spentTotalTokensInThisClass;
    @Getter
    private String model;


    private void init() {
        doc = getStyledDocument();
        setEditable(false);
        setFont(new Font("", 0, 15));
        DefaultCaret defaultCaret = (DefaultCaret) getCaret();
        defaultCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public boolean CheckIsComplete() {
        if (messages == null) return false;
        if (doc == null) return false;
        if (getModel() == null) return false;
        return true;
    }

    /**
     * 创建聊天界面
     *
     * @param model 服务器模型
     */
    public ChatFiled(String model, ArrayList<ChatBySend.ChatBySendMessage> messages) {
        this.model = model;
        this.messages = messages;
        init();
    }

    public void sender(String message) {
        if (message.isBlank()) return;
        insertText("用户输入：" + message + "\n----------------------------\n", Color.PINK, getFont().getSize(), StyleConstants.ALIGN_LEFT);
        new Thread(() -> {
            handle(message);
        }).start();
    }

    public void clearContent() {
        boolean isClear = messages.isEmpty();
        messages.clear();
        if (!isClear)
            insertText("\n---------以下为新对话---------\n", Color.blue, getFont().getSize() + 5, StyleConstants.ALIGN_CENTER);

    }

    public void clearChatFiled() {
        setText("");
    }

    public void insertText(String text/*文本内容*/, Color colorName/*文本颜色*/, int textSize/*文本大小*/, int textAlign/*对齐方式*/) {
        insertText(text, colorName, Font_Normal, textSize, textAlign);
    }

    public void insertText(String text/*文本内容*/, Color colorName/*文本颜色*/, int textStyles/*文本样式*/, int textSize/*文本大小*/, int textAlign/*对齐方式*/) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, colorName);//设置文本颜色
        StyleConstants.setFontSize(set, textSize);//设置文本大小
        StyleConstants.setAlignment(set, textAlign);//设置文本对齐方式
        doc.setParagraphAttributes(getText().length(), doc.getLength() - getText().length(), set, false);
        switch (textStyles) {
            case Font_italic -> StyleConstants.setItalic(set, true);
            case Font_Bold -> StyleConstants.setBold(set, true);
            case Font_BoldItalics -> {
                StyleConstants.setBold(set, true);
                StyleConstants.setItalic(set, true);
            }
            case Font_UnderLine -> StyleConstants.setUnderline(set, true);
        }
        try {
            doc.insertString(doc.getLength(), text, set);//插入文本
        } catch (BadLocationException e) {
            log.error(Logger.getExceptionMessage(e));
        }
    }

    private void handle(String message) {
        currentGenerateContent = new StringBuffer();
        messages.add(new ChatBySend.ChatBySendMessage(message, "user"));
        try (Response response = MainJavaFXRunner.mainJavaFXRunner.connection.sendByCurrentThread(MainJavaFXRunner.mainJavaFXRunner.manager.getSender().getRequest(MainJavaFXRunner.mainJavaFXRunner.chatBySend))) {
            if (!response.isSuccessful()) {
                System.err.print("响应错误:");
                switch (response.code()) {
                    case 400 ->
                            insertText("格式错误（400）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    case 401 ->
                            insertText("认证失败（401）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    case 402 ->
                            insertText("余额不足（402）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    case 422 ->
                            insertText("参数错误（422）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    case 429 ->
                            insertText("请求速率达到上限（429）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    case 500 ->
                            insertText("服务器故障（500）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    case 503 ->
                            insertText("服务器繁忙（503）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                    default ->
                            insertText("未知错误（" + response.code() + "）\n", Color.RED, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                }
                doWorkAfterAnswering();
                return;
            }

            // 4. 流式读取响应体
            try (BufferedSource source = response.body().source()) {
                while (!source.exhausted()) {
                    String line = source.readUtf8Line();
                    if (line == null) continue;

                    // 5. 处理SSE格式数据
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6).trim();
                        if (json.equals("[DONE]")) {
                            insertText("[流结束]\n", Color.GRAY, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                            messages.add(new ChatBySend.ChatBySendMessage(currentGenerateContent.toString(), "assistant"));
                            doWorkAfterAnswering();
                            continue;
                        }
                        // 6. 解析JSON内容（示例使用简单解析，生产环境建议用Gson/Jackson）
                        parseAndPrintContent(json);
                    }
                }
            } catch (Exception e) {
                log.error(Logger.getExceptionMessage(e));
            } finally {
                response.close(); // 确保释放资源
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doWorkAfterAnswering() {
        SwingUtilities.invokeLater(() -> {
            ChatController chatController = MainJavaFXRunner.mainJavaFXRunner.Chat.getController();
            chatController.saveObjectToFile(ChatController.file);
        });
    }

    private void parseAndPrintContent(String json) {
        try {
            // 0. 预处理JSON字符串
            if (json.startsWith("data: ")) {
                json = json.substring(6).trim();
            }
            FlowChatByReceive flowChatByReceive = Sender.gson.fromJson(json, FlowChatByReceive.class);
            if (flowChatByReceive == null) return;
            FlowChatByReceive.Delta delta = flowChatByReceive.getChoices().getFirst().getDelta();
            String Reasoning_content = delta.getReasoning_content();
            String Content = delta.getContent();
            Reasoning_content = Reasoning_content == null ? null : Reasoning_content.replace("\n\n", "\n        ");
//            Content = Content == null ? null : Content.replace("\n\n", "\n");
            FlowChatByReceive.Usage usage = flowChatByReceive.getUsage();
            if (usage != null) {
                StringBuilder endOutPut = new StringBuilder();
                int total_tokens = usage.getTotal_tokens();
                int prompt_tokens = usage.getPrompt_tokens();
                int prompt_cache_hit_tokens = usage.getPrompt_cache_hit_tokens();
                int prompt_cache_miss_tokens = usage.getPrompt_cache_miss_tokens();
                int completion_tokens = usage.getCompletion_tokens();
                int reasoning_tokens = usage.getPrompt_tokens_details().getCached_tokens();
                endOutPut
                        .append("\n-----------------------------------------------")
                        .append("\n消耗总tokens数：                      ").append(total_tokens)
                        .append("\n---用户prompt所包含的token数：         ").append(prompt_tokens)
                        .append("\n------缓存命中tokens数：              ").append(prompt_cache_hit_tokens)
                        .append("\n------缓存未命中tokens数：            ").append(prompt_cache_miss_tokens)
                        .append("\n---模型completion产生的token数：      ").append(completion_tokens)
                        .append("\n------推理模型所产生的思维链token数：   ").append(reasoning_tokens)
                        .append("\n-----------------------------------------------")
                        .append("\n");
                insertText(endOutPut.toString(), Color.GREEN, getFont().getSize(), StyleConstants.ALIGN_LEFT);

                spentTotalTokensInThisObject += total_tokens;
                spentTotalTokensInThisClass += total_tokens;


            }
            handleContentUpdates(Reasoning_content, Content);
        } catch (JsonSyntaxException e) {
            log.error(Logger.getExceptionMessage(e));
        }
    }


    // 处理内容更新的方法
    private void handleContentUpdates(String reasoningContent, String content) {
        // 处理推理内容
        if (reasoningContent != null && !reasoningContent.isEmpty()) {
            if (!isThink) {
                insertText("Think:\n", Color.ORANGE, getFont().getSize() + 4, StyleConstants.ALIGN_LEFT);
                insertText("        ", Color.BLACK, getFont().getSize(), StyleConstants.ALIGN_LEFT);
                startToThinkTime = System.currentTimeMillis();
                isThink = true;
            }
            insertText(reasoningContent, Color.DARK_GRAY, getFont().getSize(), StyleConstants.ALIGN_LEFT);
        }

        // 处理正式内容
        if (content != null && !content.isEmpty()) {
            if (isThink) {
                insertText("\nThink Finished. Start to Generate content!(Took " + (System.currentTimeMillis() - startToThinkTime) / 1000 + " s)\n", Color.ORANGE, getFont().getSize() + 4, StyleConstants.ALIGN_LEFT);
                if (startToThinkTime != 0) {
                    startToThinkTime = 0;
                }
                isThink = false;
            }

            if (content.contains("\n\n")) {
                Font_Styles = Font_Normal;
                Font_Size = 0;
                content.replace("\n\n", "\n");
            }
            insertText(content, Color.BLACK, Font_Size, getFont().getSize() + Font_Size, StyleConstants.ALIGN_LEFT);
            currentGenerateContent.append(content);
        }
    }


}

