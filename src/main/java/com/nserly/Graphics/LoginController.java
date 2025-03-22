package com.nserly.Graphics;

import com.nserly.Tools.Connection.MessageCollections.DeepSeek.GetModelByReceive;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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

    private ObservableList<String> ModelLists;


    @FXML
    public void Login() {
        Main.init.ChangeValue("Model", (String) Model.getValue());
        Main.init.ChangeValue("ServerAPI", APIKey.getText());
        Main.init.Update();
        log.info("Save is complete!");
        log.info("Loading chat GUI...");
        Main.main.SwitchToMainUI();
    }

    @FXML
    public void ModelClicked() throws Exception {
        if (APIKey.getText().isBlank() || APIKey.getText().equals(lastAPIKey)) return;
        ArrayList<GetModelByReceive.ModelDescribe> arrayList = Main.main.manager.getSupportModel();
        Model.getItems().clear();
        arrayList.forEach(e -> {
            Model.getItems().add(e.getId());
        });
        lastAPIKey = APIKey.getText();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            String ServerAPI = (String) Main.init.getValue("ServerAPI");
            String modelName = (String) Main.init.getValue("Model");
            if (ServerAPI != null && !ServerAPI.isBlank()) {
                APIKey.setText(ServerAPI);
                lastAPIKey = APIKey.getText();
                try {
                    ArrayList<GetModelByReceive.ModelDescribe> arrayList = Main.main.manager.getSupportModel();
                    Platform.runLater(() -> {
                        arrayList.forEach(e -> {
                            Model.getItems().add(e.getId());
                        });
                        if (modelName != null && Model.getItems().contains(modelName)) {
                            Model.setValue(modelName);
                        }
                    });
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }).start();

    }
}
