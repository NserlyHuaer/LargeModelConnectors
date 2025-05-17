package com.nserly.Controller;

import com.nserly.Logger;
import com.nserly.MainJavaFXRunner;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.InformationPostOrGet.GetModelByReceive;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Slf4j
public class LoginController implements Initializable {
    @FXML
    private TextField APIKey;
    @FXML
    private ChoiceBox Model;
    private String lastAPIKey;

    public static ArrayList<GetModelByReceive.ModelDescribe> arrayList;


    @FXML
    public void Login() {
        MainJavaFXRunner.init.ChangeValue("Model", (String) Model.getValue());
        MainJavaFXRunner.init.ChangeValue("ServerAPI", APIKey.getText());
        MainJavaFXRunner.init.Update();
        log.info("Save is complete!");
        log.info("Loading chat GUI...");
        MainJavaFXRunner.mainJavaFXRunner.SwitchToMainUI();
    }

    @FXML
    public void ModelClicked() throws Exception {
        if (APIKey.getText().isBlank() || APIKey.getText().equals(lastAPIKey)) return;
        ArrayList<GetModelByReceive.ModelDescribe> arrayList = MainJavaFXRunner.mainJavaFXRunner.manager.getSupportModel();
        Model.getItems().clear();
        arrayList.forEach(e -> {
            Model.getItems().add(e.getId());
        });
        lastAPIKey = APIKey.getText();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            String ServerAPI = (String) MainJavaFXRunner.init.getValue("ServerAPI");
            String modelName = (String) MainJavaFXRunner.init.getValue("Model");
            String StartNewDialogue = (String) MainJavaFXRunner.init.getValue("StartNewDialogue");
            if (!ServerAPI.isBlank()) {
                APIKey.setText(ServerAPI);
                lastAPIKey = APIKey.getText();
                try {
                    arrayList = MainJavaFXRunner.mainJavaFXRunner.manager.getSupportModel();
                    Platform.runLater(() -> {
                        arrayList.forEach(e -> {
                            Model.getItems().add(e.getId());
                        });
                        if (Model.getItems().contains(modelName)) {
                            Model.setValue(modelName);
                        }
                    });
                } catch (IOException e) {
                    log.error(Logger.getExceptionMessage(e));
                }
            }
            StartNewDialogue = StartNewDialogue.equalsIgnoreCase("true") ? "true" : "false";
            MainJavaFXRunner.init.ChangeValue("StartNewDialogue", StartNewDialogue);
            MainJavaFXRunner.init.Update();
        }).start();

    }
}
