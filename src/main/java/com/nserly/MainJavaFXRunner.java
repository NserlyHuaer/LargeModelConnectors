package com.nserly;

import com.nserly.ConnectionManager.DeepSeek.Manager;
import com.nserly.Controller.LoginController;
import com.nserly.Controller.MainController;
import com.nserly.Init.Init;
import com.nserly.Tools.Connection.Connection;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatBySend;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class MainJavaFXRunner extends Application {
    private final FXMLLoader LoginUI = new FXMLLoader(getClass().getResource("/com/nserly/Graphics/Login.fxml"));
    private final FXMLLoader MainUI = new FXMLLoader(getClass().getResource("/com/nserly/Graphics/Main.fxml"));

    public final FXMLLoader Settings = new FXMLLoader(getClass().getResource("/com/nserly/Graphics/Settings.fxml"));

    public final FXMLLoader Chat = new FXMLLoader(getClass().getResource("/com/nserly/Graphics/Chat.fxml"));
    public final FXMLLoader Status = new FXMLLoader(getClass().getResource("/com/nserly/Graphics/Status.fxml"));

    private Scene LoginScene;
    private Scene MainScene;
    public Node SettingsNode;
    public Node ChatNode;
    public Node StatusNode;

    public static Init<String, String> init;

    public static MainJavaFXRunner mainJavaFXRunner;

    public Manager manager;
    public ChatBySend chatBySend;
    @Getter
    private Stage stage;

    public Connection connection;

    public static final HashMap<String, String> DEFAULT_SETTINGS_VALUE;

    static {
        DEFAULT_SETTINGS_VALUE = new HashMap<>();
        DEFAULT_SETTINGS_VALUE.put("Model", Manager.UsualModels.getFirst());
        DEFAULT_SETTINGS_VALUE.put("ServerAPI", "");
        DEFAULT_SETTINGS_VALUE.put("StartNewDialogue", "false");
        DEFAULT_SETTINGS_VALUE.put("Temperature", "1");
        DEFAULT_SETTINGS_VALUE.put("CurrentDialogueSpentTokensCount", "0");
        System.setProperty("sun.java2d.opengl", "true");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        stage.setScene(LoginScene);
        stage.setTitle("DeepSeek连接器（尝鲜版1.0）");
        stage.show();
    }

    @Override
    public void init() throws Exception {
        mainJavaFXRunner = this;
        Init.init();
        init = new Init<>("data/Information.properties");
        init.InitializeNoneValue(DEFAULT_SETTINGS_VALUE);
        manager = new Manager((String) MainJavaFXRunner.init.getValue("ServerAPI"), null);
        connection = new Connection(null);
        Platform.runLater(() -> {
            try {
                LoginScene = new Scene(LoginUI.load());
                MainScene = new Scene(MainUI.load());
                SettingsNode = Settings.load();
                ChatNode = Chat.load();
                StatusNode = Status.load();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        super.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void SwitchToMainUI() {
        chatBySend.setStream(true);
        chatBySend.setStream_options(new ChatBySend.Stream_options(true));
        chatBySend.setModel((String) init.getValue("Model"));
        manager.getSender().setAPI_Key((String) init.getValue("ServerAPI"));
        chatBySend.setTemperature((String) init.getValue("Temperature"));
        stage.setScene(MainScene);
        MainController mainController = MainUI.getController();
        if (LoginController.arrayList != null && !LoginController.arrayList.isEmpty()) {
            LoginController.arrayList.forEach(e -> {
                mainController.addChatGroup(e.getId());
            });
        } else {
            Manager.UsualModels.forEach(mainController::addChatGroup);
        }
        mainController.SelectGroup((String) init.getValue("Model"));
        stage.setX(450);
        stage.setY(120);
        stage.centerOnScreen();
    }
}