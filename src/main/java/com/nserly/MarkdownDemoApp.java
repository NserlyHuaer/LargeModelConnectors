package com.nserly;

import com.nserly.Tools.Component.EfficientMarkdownView;
import javafx.scene.control.SplitPane;

public class MarkdownDemoApp extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(javafx.stage.Stage stage) {
        EfficientMarkdownView mdView = new EfficientMarkdownView();

        // 创建动态绑定的文本区域
        javafx.scene.control.TextArea editor = new javafx.scene.control.TextArea();
        editor.setWrapText(true);

        // 绑定双向更新
        javafx.beans.property.StringProperty textProperty = editor.textProperty();
        mdView.bindToTextProperty(textProperty);

        // 布局
        javafx.scene.Scene scene = new javafx.scene.Scene(
                new SplitPane(editor, mdView), 1200, 800);
        stage.setScene(scene);
        stage.show();
    }
}
