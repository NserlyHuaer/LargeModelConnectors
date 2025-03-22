package com.nserly.Console;

import com.google.gson.*;
import com.nserly.ConnectionManager.DeepSeek.Manager;
import com.nserly.Init.Init;
import com.nserly.Tools.Connection.Connection;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatBySend;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.CheckBalanceByReceive;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.FlowChatByReceive;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.GetModelByReceive;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

@Slf4j
public class Main {
    private static String ServerAddress;
    private static String ServerAPI;
    private static String Temperature;
    private static String Model;

    private static boolean isFinished = true;

    private static final String help = "当前存在一下命令：\n" +
            "1. /setModel [Model]                        设置当前模型\n" +
            "2. /getCurrentModel                         获取当前设置的模型\n" +
            "3. /getAllModels                            获取当前api账户支持的模型\n" +
            "4. /setTemperature [Temperature]（0~2）      设置当前温度\n" +
            "5. /getTemperature                          获取当前温度\n" +
            "6. /getBalance                              获取api账户余额\n" +
            "7. /clear                                   清除上下文\n" +
            "8. /help                                    获取帮助\n" +
            "9. /exit                                    退出程序\n";
    private static final ChatBySend chatBySend = new ChatBySend();
    private static final ArrayList<ChatBySend.ChatBySendMessage> messages = new ArrayList<>();
    private static StringBuffer currentGenerateContent;
    private static StringBuffer endOutPut;
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY) // 字段名直接匹配 JSON key
            .enableComplexMapKeySerialization()
            .create();
    private static Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.err.println("请求失败: " + e.getMessage());
            System.out.print("请输入命令：");
            isFinished = true;
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            if (!response.isSuccessful()) {
                System.err.print("响应错误:");
                switch (response.code()) {
                    case 400 -> System.err.println("格式错误（400）");
                    case 401 -> System.err.println("认证失败（401）");
                    case 402 -> System.err.println("余额不足（402）");
                    case 422 -> System.err.println("参数错误（422）");
                    case 429 -> System.err.println("请求速率达到上限（429）");
                    case 500 -> System.err.println("服务器故障（500）");
                    case 503 -> System.err.println("服务器繁忙（503）");
                    default -> System.out.println("未知错误（" + response.code() + "）");
                }
                if (endOutPut != null && !endOutPut.isEmpty()) System.out.println(endOutPut);
                endOutPut = null;
                System.out.print("请输入命令：");
                isFinished = true;
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
                            if (endOutPut != null && !endOutPut.isEmpty()) System.out.print("\n" + endOutPut);
                            endOutPut = null;
                            System.out.println("\n[流结束]");
                            messages.add(new ChatBySend.ChatBySendMessage(currentGenerateContent.toString(), "assistant"));
                            continue;
                        }

                        // 6. 解析JSON内容（示例使用简单解析，生产环境建议用Gson/Jackson）
                        parseAndPrintContent(json);
                    }
                }
            } catch (Exception e) {
                System.err.println("流处理异常: " + e.getMessage());
            } finally {
                response.close(); // 确保释放资源
                System.out.print("请输入命令：");
                isFinished = true;
            }
        }
    };

    private static void parseAndPrintContent(String json) {
        try {
            // 0. 预处理JSON字符串
            if (json.startsWith("data: ")) {
                json = json.substring(6).trim();
            }
            FlowChatByReceive flowChatByReceive = gson.fromJson(json, FlowChatByReceive.class);
            if (flowChatByReceive == null) return;
            FlowChatByReceive.Delta delta = flowChatByReceive.getChoices().getFirst().getDelta();
            String Reasoning_content = delta.getReasoning_content();
            String Content = delta.getContent();
            Reasoning_content = Reasoning_content == null ? null : Reasoning_content.replace("\n\n", "\n");
            Content = Content == null ? null : Content.replace("\n\n", "\n");
            FlowChatByReceive.Usage usage = flowChatByReceive.getUsage();
            if (usage != null) {
                endOutPut = new StringBuffer();
                int total_tokens = usage.getTotal_tokens();

                int prompt_tokens = usage.getPrompt_tokens();
                int prompt_cache_hit_tokens = usage.getPrompt_cache_hit_tokens();
                int prompt_cache_miss_tokens = usage.getPrompt_cache_miss_tokens();

                int completion_tokens = usage.getCompletion_tokens();
                int reasoning_tokens = usage.getPrompt_tokens_details().getCached_tokens();
                endOutPut
                        .append("\n-----------------------------------------------")
                        .append("\n消耗总tokens数：\t\t\t\t\t\t").append(total_tokens)
                        .append("\n---用户prompt所包含的token数：\t\t\t").append(prompt_tokens)
                        .append("\n------缓存命中tokens数：\t\t\t\t").append(prompt_cache_hit_tokens)
                        .append("\n------缓存未命中tokens数：\t\t\t").append(prompt_cache_miss_tokens)
                        .append("\n---模型completion产生的token数：\t\t").append(completion_tokens)
                        .append("\n------推理模型所产生的思维链token数：\t").append(reasoning_tokens)
                        .append("\n-----------------------------------------------");
            }
            handleContentUpdates(Reasoning_content, Content);
        } catch (JsonSyntaxException e) {
            System.err.println("JSON解析错误: " + e.getMessage());
        }
    }

    private static boolean isThink;
    private static long startToThinkTime;

    private static Manager manager;

    // 处理内容更新的方法
    private static void handleContentUpdates(String reasoningContent, String content) {
        // 保证在事件调度线程更新UI（Swing要求）
        StringBuilder buffer = new StringBuilder();

        // 处理推理内容
        if (reasoningContent != null && !reasoningContent.isEmpty()) {
            if (!isThink) {
                buffer.append("\nThink:\n");
                startToThinkTime = System.currentTimeMillis();
                isThink = true;
            }
            buffer.append(reasoningContent);
        }

        // 处理正式内容
        if (content != null && !content.isEmpty()) {
            if (isThink) {
                buffer.append("\nThink Finished. Start to Generate content!");
                if (startToThinkTime != 0) {
                    buffer.append("(Spent ").append((System.currentTimeMillis() - startToThinkTime) / 1000).append("s)");
                    startToThinkTime = 0;
                }
                buffer.append("\n");
                isThink = false;
            }
            buffer.append(content);
            currentGenerateContent.append(content);
        }

        // 更新文本面板
        if (!buffer.isEmpty()) {
            System.out.print(buffer);
        }
    }

    private static Connection connection;

    public static void start() throws IOException {
        log.info("Project started from the console");
        Init.init();
        Init<String, String> init = new Init<>("data/Information.properties");
        init.Loading();
        boolean isMustInputInformation = false;
        ServerAddress = (String) init.getValue("ServerAddress");
        ServerAPI = (String) init.getValue("ServerAPI");
        Temperature = (String) init.getValue("Temperature");
        Model = (String) init.getValue("Model");
        if (Temperature == null || Temperature.isBlank()) {
            Temperature = "1";
        }
        if (ServerAddress == null || ServerAddress.isBlank()) isMustInputInformation = true;
        if (ServerAPI == null || ServerAPI.isBlank()) isMustInputInformation = true;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入一下命令：退出程序：/exit  运行之前的配置：/before");
            if (isMustInputInformation) {
                inputInformationFromConsole(scanner);
            } else {
                System.out.println("请输入是否运行之前的配置：Y/N");
                if (!isRunnerLastBefore(scanner)) {
                    inputInformationFromConsole(scanner);
                }
            }
            if (Model == null || Model.isBlank()) Model = "deepseek-chat";
            init.ChangeValue("ServerAddress", ServerAddress);
            init.ChangeValue("ServerAPI", ServerAPI);
            init.ChangeValue("Temperature", Temperature);
            init.ChangeValue("Model", Model);
            init.Update();
            manager = new Manager(ServerAPI, null);
            chatBySend.setStream(true);
            chatBySend.setMessages(messages);
            connection = new Connection(callback);
            chatBySend.setTemperature(Double.parseDouble(Temperature));
            chatBySend.setModel(Model);
            chatBySend.setStream_options(new ChatBySend.Stream_options(true));
            System.out.println("连接成功，请输入发送的信息");
            while (true) {
                if (isFinished) {
                    System.out.print("请输入命令：");
                }
                String message = scanner.nextLine();
                message = message.trim();
                if (message.startsWith("/")) {
                    if (message.equals("/exit")) {
                        System.exit(0);
                    } else if (message.startsWith("/setModel")) {
                        message = message.split("/setModel ", 2)[1];
                        if (!message.isBlank()) {
                            chatBySend.setModel(message);
                            init.ChangeValue("Model", message);
                            init.Update();
                            System.out.println("设置成功~新模型：" + message);
                        }
                    } else if (message.startsWith("/setTemperature")) {
                        message = message.split("/setTemperature ", 2)[1];
                        if (!message.isBlank()) {
                            chatBySend.setTemperature(Double.parseDouble(message));
                            init.ChangeValue("Temperature", message);
                            init.Update();
                            System.out.println("设置成功~当前温度：" + message);
                        }
                    } else if (message.equals("/getCurrentModel")) {
                        System.out.println("当前模型为：" + chatBySend.getModel());
                    } else if (message.equals("/getAllModels")) {
                        ArrayList<GetModelByReceive.ModelDescribe> arrayList = manager.getSupportModel();
                        if (arrayList == null) {
                            System.err.println("获取失败");
                            continue;
                        }
                        System.out.println("当前有一下模型：");

                        for (int i = 0; i < arrayList.size(); i++) {
                            System.out.println(i + 1 + "." + arrayList.get(i).getId());
                        }
                        System.out.println("--------------------");
                    } else if (message.equals("/getTemperature")) {
                        System.out.println("当前温度：" + Temperature);
                    } else if (message.equals("/getBalance")) {
                        CheckBalanceByReceive.Balance_infos balance_infos = manager.getBalance();
                        System.out.println("当前api key中剩余余额：");
                        System.out.println("1.总的可用余额：" + balance_infos.getTotal_balance() + balance_infos.getCurrency());
                        System.out.println("2.未过期的赠金余额：" + balance_infos.getGranted_balance() + balance_infos.getCurrency());
                        System.out.println("3.充值余额：" + balance_infos.getTopped_up_balance() + balance_infos.getCurrency());
                        System.out.println("-------------------------");


                    } else if (message.equals("/clear")) {
                        messages.clear();
                        System.out.println("清除上下文成功");
                    } else if (message.equals("/help")) {
                        System.out.println(help);
                    } else {
                        System.out.println("命令未找到");
                    }
                } else if (isFinished && !message.isBlank()) {
                    currentGenerateContent = new StringBuffer();
                    messages.add(new ChatBySend.ChatBySendMessage(message, "user"));
                    connection.sendByNewThread(manager.getSender().getRequest(chatBySend));
                    isFinished = false;
                }
            }
        }
    }

    private static void inputInformationFromConsole(Scanner scanner) {
        System.out.println("请输入服务器地址：");
        ServerAddress = scanner.nextLine();
        System.out.println("请输入API Key：");
        ServerAPI = scanner.nextLine();
    }

    private static boolean isRunnerLastBefore(Scanner sc) {
        String a = sc.next();
        if (a.equalsIgnoreCase("Y")) return true;
        else if (a.equalsIgnoreCase("TRUE")) return true;
        else if (a.equalsIgnoreCase("YES")) return true;
        else if (a.equalsIgnoreCase("/before")) return true;
        else return false;
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}

