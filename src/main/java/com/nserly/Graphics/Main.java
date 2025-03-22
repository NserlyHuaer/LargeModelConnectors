package com.nserly.Graphics;

import com.nserly.Init.Init;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private final FXMLLoader LoginUI = new FXMLLoader(getClass().getResource("Login.fxml"));
    private final FXMLLoader MainUI = new FXMLLoader(getClass().getResource("Main.fxml"));

    private Scene LoginScene;
    private Scene MainScene;

    public static Init<String, String> init;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(LoginScene);
        stage.show();

    }

    @Override
    public void init() throws Exception {
        Init.init();
        init = new Init<>("data/Information.properties");
        init.Run();
        LoginScene = new Scene(LoginUI.load());
        MainScene = new Scene(MainUI.load());
        super.init();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}