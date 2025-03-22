package com.nserly.Graphics;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML
    private TextField APIKey;
    @FXML
    private ChoiceBox Model;

    private ObservableList<String> ModelLists;


    @FXML
    public void Login() {
        System.out.println("---" + Model.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void ModelClicked() {
        if (APIKey.getText().isBlank()) return;

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String srt = (String) Main.init.getValue("ServerAPI");
        APIKey.setText(srt);
    }
}
