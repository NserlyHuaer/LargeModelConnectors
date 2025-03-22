package com.nserly.Graphics;

import com.nserly.ConnectionManager.DeepSeek.Manager;
import com.nserly.Init.Init;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatBySend;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Main extends Application {
    private final FXMLLoader LoginUI = new FXMLLoader(getClass().getResource("Login.fxml"));
    private final FXMLLoader MainUI = new FXMLLoader(getClass().getResource("Main.fxml"));

    private Scene LoginScene;
    private Scene MainScene;

    public static Init<String, String> init;

    public static Main main;

    public Manager manager;
    public ChatBySend chatBySend;
    private Stage stage;

    private static final Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

        }
    };

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        stage.setScene(LoginScene);
        stage.show();

    }

    @Override
    public void init() throws Exception {
        main = this;
        Init.init();
        init = new Init<>("data/Information.properties");
        init.Run();
        manager = new Manager((String) Main.init.getValue("ServerAPI"), callback);
        Platform.runLater(()->{
            try {
                LoginScene = new Scene(LoginUI.load());
                MainScene = new Scene(MainUI.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        super.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void SwitchToMainUI() {
        stage.setScene(MainScene);
        stage.setX(450);
        stage.setY(120);
    }
}