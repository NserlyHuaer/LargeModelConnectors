package com.nserly.Graphics;

import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatBySend;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public WebView webView;
    @FXML
    public Button SendButton;
    @FXML
    public TextField textField;
    public StringBuffer last;
    private static final String CSS_STYLE = """
            body { font-family: Arial; line-height: 1.6 }
            code { background: #f4f4f4; padding: 1px 3px }
            pre { background: #f8f8f8; padding: 5px }
            """;

    @FXML
    public void SendButtonClicked() {
        new Thread(() -> {
            if (textField.getText().isBlank()) return;
            last.append("用户输入：").append(textField.getText()).append("\n");
//            Main.main.chatBySend.getMessages().add(new ChatBySend.ChatBySendMessage(textField.getText(), "user"));
//            Main.main.manager.getSender().getRequest(Main.main.chatBySend);
            String htmlContent = wrapHtml(markdownToHtml(last.toString()));
            Platform.runLater(() -> {
                webView.getEngine().loadContent(htmlContent);
            });
        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.main.chatBySend = new ChatBySend();
        last = new StringBuffer();
    }

    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private String wrapHtml(String body) {
        return String.format("<html><style>%s</style><body>%s</body></html>",
                CSS_STYLE, body);

    }

}
