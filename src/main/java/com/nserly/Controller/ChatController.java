package com.nserly.Controller;

import com.nserly.ConnectionManager.DeepSeek.Manager;
import com.nserly.Graphics.AlertLikeJOptionPane;
import com.nserly.Graphics.ChatFiled;
import com.nserly.MainJavaFXRunner;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatBySend;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;


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


    private SwingNode lastSwingNode;

    public void setModel(String Model) {
        if (chatList.containsKey(Model) && Model.equals(getModel())) return;
        ModelNameLabel.setText(Model);
        ChatArea.getChildren().remove(lastSwingNode);
        ChatFiled chatFiled = chatList.get(Model) == null ? null : (ChatFiled) chatList.get(Model).getFirst();
        SwingNode swingNode = chatList.get(Model) == null ? null : (SwingNode) chatList.get(Model).get(1);
        if (chatFiled == null || swingNode == null) {
            ArrayList<Object> value = new ArrayList<>();
            ArrayList<ChatBySend.ChatBySendMessage> messages = new ArrayList<>();
            chatFiled = new ChatFiled(Model, messages);
            swingNode = new SwingNode();
            swingNode.setContent(new JScrollPane(chatFiled));
            value.add(chatFiled);
            value.add(swingNode);
            value.add(messages);
            chatList.put(Model, value);
        }
        ChatArea.setCenter(swingNode);
        lastSwingNode = swingNode;
    }

    public String getModel() {
        return ModelNameLabel.getText();
    }

    public void Send(String message) {
        ChatFiled chatFiled = (ChatFiled) chatList.get(getModel()).getFirst();
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
            if (chatFiled != null) chatFiled.clearContent();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TypeMessageField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                SendAction();
            }
        });
        String Model = (String) MainJavaFXRunner.init.getValue("Model");
        ModelNameLabel.setText((Model != null && !Model.isBlank()) ? Model : Manager.UsualModels.getFirst());
        chatList = new TreeMap<>();

        if (MainJavaFXRunner.init.getValue("StartNewDialogue").toString().toLowerCase().equals("true"))
            readObjectFromFile(file);
    }

    public void saveObjectToFile(File file) {
        if (!file.exists()) return;
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(chatList);
            MainJavaFXRunner.init.ChangeValue("CurrentDialogueSpentTokensCount", String.valueOf(ChatFiled.getSpentTotalTokensInThisClass()));
            MainJavaFXRunner.init.Update();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void readObjectFromFile(File file) {
        if (!file.exists()) return;
        //尝试通过反序列化获取上次的状态
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            chatList = (TreeMap<String, ArrayList<Object>>) objectInputStream.readObject();
            ChatFiled.setSpentTotalTokensInThisClass(Long.parseLong(MainJavaFXRunner.init.getValue("CurrentDialogueSpentTokensCount").toString()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
