package com.nserly.Controller;

import com.nserly.Graphics.AlertLikeJOptionPane;
import com.nserly.Graphics.ChatFiled;
import com.nserly.Logger;
import com.nserly.MainJavaFXRunner;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatMessage.ChatBySend;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;


@Slf4j
public class ChatController implements Initializable {
    public static final File file = new File("data/chat/chatInfo.obj");
    @FXML
    public Label ModelNameLabel;

    @FXML
    public BorderPane ChatArea;

    @FXML
    public Button StartNewTopic;

    @FXML
    public TextField TypeMessageField;

    @FXML
    public Button SenderButton;

    @Getter
    //TreeMap(模型名称;ArrayList(ChatFiled;SwingNode;ArrayList<ChatBySend.ChatBySendMessage>))
    private TreeMap<String, ArrayList<Object>> chatList;

    private boolean isNeedToSaveObject;


    private SwingNode lastSwingNode;

    public void setModel(String Model) {
        if (chatList.containsKey(Model) && Model.equals(getModel())) return;
        ModelNameLabel.setText(Model);
        ChatFiled chatFiled = chatList.get(Model) == null ? null : (ChatFiled) chatList.get(Model).getFirst();
        SwingNode swingNode = chatList.get(Model) == null ? null : (SwingNode) chatList.get(Model).get(1);
        if (chatFiled != null && !chatFiled.CheckIsComplete()) {
            chatFiled = null;
            chatList.remove(Model);
        }
        if (chatFiled == null || swingNode == null) {
            ArrayList<Object> value = new ArrayList<>();
            ArrayList<ChatBySend.ChatBySendMessage> messages = new ArrayList<>();
            if (chatFiled == null)
                chatFiled = new ChatFiled(Model, messages);
            swingNode = new SwingNode();
            swingNode.setContent(new JScrollPane(chatFiled));
            value.add(chatFiled);
            value.add(swingNode);
            value.add(messages);
            chatList.put(Model, value);
        }
        SwingNode finalSwingNode = swingNode;
        Platform.runLater(() -> {
            ChatArea.getChildren().remove(lastSwingNode);
            ChatArea.setCenter(finalSwingNode);
        });
        lastSwingNode = swingNode;
        MainJavaFXRunner.mainJavaFXRunner.chatBySend.setMessages(chatFiled.getMessages());
        MainJavaFXRunner.mainJavaFXRunner.chatBySend.setModel(chatFiled.getModel());
    }

    public String getModel() {
        return ModelNameLabel.getText();
    }

    public void Send(String message) {
        ChatFiled chatFiled = (ChatFiled) chatList.get(getModel()).getFirst();
        isNeedToSaveObject = true;
        if (chatFiled != null) {
            chatFiled.sender(message);
        }
    }

    @FXML
    private void SendAction() {
        if (TypeMessageField.getText().isBlank()) return;
        if (AlertLikeJOptionPane.showConfirmDialog(MainJavaFXRunner.mainJavaFXRunner.getStage(), "发送", "是否确定发送？", "发送", AlertLikeJOptionPane.YES_NO_OPTION) == AlertLikeJOptionPane.YES_OPTION) {
            Send(TypeMessageField.getText());
            TypeMessageField.setText("");
        }
    }

    @FXML
    private void startNewTopicAction() {
        if (AlertLikeJOptionPane.showConfirmDialog(MainJavaFXRunner.mainJavaFXRunner.getStage(), "开启新对话？", "是否确定开启新对话？", "开启新对话", AlertLikeJOptionPane.YES_NO_OPTION) == AlertLikeJOptionPane.YES_OPTION) {
            ChatFiled chatFiled = (ChatFiled) chatList.get(ModelNameLabel.getText()).getFirst();
            if (chatFiled != null) {
                chatFiled.clearContent();
                isNeedToSaveObject = true;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TypeMessageField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                SendAction();
            }
        });
        chatList = new TreeMap<>();
        readObjectFromFile(file);

        new Thread(() -> {
            if (MainJavaFXRunner.init.getValue("StartNewDialogue").toString().toLowerCase().equals("true")) {
                chatList.keySet().forEach(e -> {
                    ArrayList<Object> arrayList = chatList.get(e);
                    if (arrayList != null) {
                        ChatFiled chatFiled = (ChatFiled) arrayList.getFirst();
                        if (chatFiled != null) {
                            isNeedToSaveObject = true;
                            chatFiled.clearContent();
                        }
                    }
                });
            }
        }).start();

    }

    public void saveObjectToFile(File file) {
        if (!isNeedToSaveObject) return;
        if (!file.exists()) return;
        log.info("Start Writing Object to File...");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ArrayList<Object> arrayList = new ArrayList<>();
            Set<String> sets = chatList.keySet();
            ArrayList<String> appeared = new ArrayList<>();
            for (String set : sets) {
                ChatFiled chatFiled = (ChatFiled) chatList.get(set).getFirst();
                if (appeared.contains(chatFiled.getModel())) continue;
                appeared.add(chatFiled.getModel());
                arrayList.add(chatFiled);
            }
            objectOutputStream.writeObject(arrayList);
            MainJavaFXRunner.init.ChangeValue("CurrentDialogueSpentTokensCount", String.valueOf(ChatFiled.getSpentTotalTokensInThisClass()));
            MainJavaFXRunner.init.Update();
            log.info("Write Object to File Completed successfully!");
        } catch (Exception e) {
            log.error(Logger.getExceptionMessage(e));
            log.error("Write not done,thrown Exception(s)");
        }
        isNeedToSaveObject = false;
    }

    public void readObjectFromFile(File file) {
        if (!file.exists()) return;
        //尝试通过反序列化获取上次的状态
        log.info("Start to reading object from file...");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<ChatFiled> source = (ArrayList<ChatFiled>) objectInputStream.readObject();
            ArrayList<Object> cache = new ArrayList<>();
            SwingNode swingNode;
            for (ChatFiled arr : source) {
                swingNode = new SwingNode();
                swingNode.setContent(new JScrollPane(arr));
                cache.add(arr);
                cache.add(swingNode);
                cache.add(arr.getMessages());
                chatList.remove(arr.getModel());
                chatList.put(arr.getModel(), cache);
                cache = new ArrayList<>();
            }
            ChatFiled.setSpentTotalTokensInThisClass(Long.parseLong(MainJavaFXRunner.init.getValue("CurrentDialogueSpentTokensCount").toString()));
            log.info("Read object to file Completed successfully!");
        } catch (Exception e) {
            log.error(Logger.getExceptionMessage(e));
            log.error("Read not done,thrown Exception(s)");
            isNeedToSaveObject = true;
            saveObjectToFile(file);
        }
    }
}
