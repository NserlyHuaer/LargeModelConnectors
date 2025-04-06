package com.nserly.Controller;

import com.nserly.ConnectionManager.DeepSeek.Manager;
import com.nserly.Graphics.AlertLikeJOptionPane;
import com.nserly.MainJavaFXRunner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.ResourceBundle;


public class SettingsController implements Initializable {
    @FXML
    public ComboBox<String> ModelComboBox;
    @FXML
    public TextField APIKeyTypeFiled;
    @FXML
    public ToggleSwitch StartNewDialogueSwitch;
    @FXML
    public Button RefreshModelListsButton;
    @FXML
    public Slider ModelTemperatureSlider;
    @FXML
    public Button setDefaultSettingsButton;
    @FXML
    public Button setCurrentSettingsButton;
    @FXML
    public Button SaveSettingsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            RefreshSettings();
            init_Listener();
        }).start();
    }

    private void RefreshSettings() {
        APIKeyTypeFiled.setText((String) MainJavaFXRunner.init.getValue("ServerAPI"));
        StartNewDialogueSwitch.setSelected(Boolean.parseBoolean((String) MainJavaFXRunner.init.getValue("StartNewDialogue")));
        ModelTemperatureSlider.setValue(Double.valueOf((String) MainJavaFXRunner.init.getValue("Temperature")));
        refreshModels();

    }

    private void refreshModels() {
        ModelComboBox.getItems().clear();
        if (LoginController.arrayList != null && !LoginController.arrayList.isEmpty()) {
            LoginController.arrayList.forEach(e -> {
                ModelComboBox.getItems().add(e.getId());
            });
        } else {
            Manager.UsualModels.forEach(e -> {
                ModelComboBox.getItems().add(e);
            });
        }
        String modelName = (String) MainJavaFXRunner.init.getValue("Model");
        ModelComboBox.setValue(modelName);
    }

    private void init_Listener() {
        RefreshModelListsButton.setOnAction(e -> {
            refreshModels();
        });
        setDefaultSettingsButton.setOnAction(e -> {
            int choice = AlertLikeJOptionPane.showConfirmDialog(MainJavaFXRunner.mainJavaFXRunner.getStage(), "设置", "确定恢复成默认设置吗？", "设置", AlertLikeJOptionPane.YES_NO_OPTION);
            if (choice == AlertLikeJOptionPane.YES_OPTION) {
                MainJavaFXRunner.DEFAULT_SETTINGS_VALUE.keySet().forEach(e1 -> {
                    MainJavaFXRunner.init.ChangeValue(e1, MainJavaFXRunner.DEFAULT_SETTINGS_VALUE.get(e1));
                });
                RefreshSettings();
            }
        });
        setCurrentSettingsButton.setOnAction(e -> {
            int choice = AlertLikeJOptionPane.showConfirmDialog(MainJavaFXRunner.mainJavaFXRunner.getStage(), "设置", "确定恢复成当前设置吗？", "设置", AlertLikeJOptionPane.YES_NO_OPTION);
            if (choice == AlertLikeJOptionPane.YES_OPTION) {
                RefreshSettings();
            }
        });
        SaveSettingsButton.setOnAction(e -> {
            int choice = AlertLikeJOptionPane.showConfirmDialog(MainJavaFXRunner.mainJavaFXRunner.getStage(), "设置","确定保存设置吗？", "设置", AlertLikeJOptionPane.YES_NO_OPTION);
            if (choice == AlertLikeJOptionPane.YES_OPTION) {
                MainJavaFXRunner.init.ChangeValue("Model", ModelComboBox.getValue());
                MainJavaFXRunner.init.ChangeValue("ServerAPI", APIKeyTypeFiled.getText());
                MainJavaFXRunner.init.ChangeValue("StartNewDialogue", String.valueOf(StartNewDialogueSwitch.isSelected()));
                MainJavaFXRunner.init.ChangeValue("Temperature", String.valueOf(ModelTemperatureSlider.getValue()));
                MainJavaFXRunner.init.Update();

                MainJavaFXRunner.mainJavaFXRunner.chatBySend.setModel((String) MainJavaFXRunner.init.getValue("Model"));
                MainJavaFXRunner.mainJavaFXRunner.manager.getSender().setAPI_Key((String) MainJavaFXRunner.init.getValue("ServerAPI"));
                MainJavaFXRunner.mainJavaFXRunner.chatBySend.setTemperature((String) MainJavaFXRunner.init.getValue("Temperature"));
            }
        });
    }
}
