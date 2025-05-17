package com.nserly.Controller;

import com.nserly.Graphics.AlertLikeJOptionPane;
import com.nserly.Graphics.ChatFiled;
import com.nserly.Logger;
import com.nserly.MainJavaFXRunner;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatMessage.ChatBySend;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.InformationPostOrGet.CheckBalanceByReceive;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatusController implements Initializable {

    @FXML
    private Label APIKeyValueLabel;

    @FXML
    private Label CurrentModelConverstationsCountLabel;

    @FXML
    private Label CurrentModelValueLabel;

    @FXML
    private Label CurrentTotalBalanceValueLabel;


    @FXML
    private Button RefreshInformationButton;

    @FXML
    private Label SpentTotalTokensValueLabel;

    @FXML
    private Label TemperatureValueLabel;

    @FXML
    void RefreshInformation() {
        refresh();
        AlertLikeJOptionPane.showConfirmDialog(MainJavaFXRunner.mainJavaFXRunner.getStage(), "状态管理", "刷新成功~", "状态管理", AlertLikeJOptionPane.YES_OPTION);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        refresh();
    }

    public void refresh() {
        APIKeyValueLabel.setText(MainJavaFXRunner.mainJavaFXRunner.manager.getSender().getAPI_Key());
        ArrayList<ChatBySend.ChatBySendMessage> arrayList = MainJavaFXRunner.mainJavaFXRunner.chatBySend.getMessages();
        CurrentModelConverstationsCountLabel.setText("0");
        if (arrayList != null)
            CurrentModelConverstationsCountLabel.setText(String.valueOf(arrayList.size() / 2));
        CurrentModelValueLabel.setText(MainJavaFXRunner.mainJavaFXRunner.chatBySend.getModel());
        SpentTotalTokensValueLabel.setText(String.valueOf(ChatFiled.getSpentTotalTokensInThisClass()));
        TemperatureValueLabel.setText(String.valueOf(MainJavaFXRunner.mainJavaFXRunner.chatBySend.getTemperature()));
        try {
            CheckBalanceByReceive.Balance_infos balance_infos = MainJavaFXRunner.mainJavaFXRunner.manager.getBalance();
            CurrentTotalBalanceValueLabel.setText(balance_infos.getTotal_balance() + " " + balance_infos.getCurrency());
        } catch (IOException ex) {
            log.error(Logger.getExceptionMessage(ex));
        }
    }
}
