package com.nserly.Controller;

import com.nserly.MainJavaFXRunner;
import com.nserly.Tools.Connection.MessageCollections.DeepSeek.ChatMessage.ChatBySend;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.net.URL;
import java.util.*;


@Slf4j
public class MainController implements Initializable {
    @FXML
    public Button SettingsButton;

    @FXML
    public Button StatusButton;
    @FXML
    public StackPane RightPane;
    @FXML
    public FlowPane ChatGroupsFlowPane;
    public StringBuffer last;

    private TreeMap<String, Button> ChatGroups;
    private static final String CSS_STYLE = """
            body { font-family: Arial; line-height: 1.2 }
            code { background: #f4f4f4; padding: 1px 1px }
            pre { background: #f8f8f8; padding: 3px }
            """;

    @FXML
    public void SettingButtonAction() {
        setRightPane(MainJavaFXRunner.mainJavaFXRunner.SettingsNode);
    }

    @FXML
    public void StatusButtonAction() {
        setRightPane(MainJavaFXRunner.mainJavaFXRunner.StatusNode);
        StatusController statusController = MainJavaFXRunner.mainJavaFXRunner.Status.getController();
        statusController.refresh();
    }

    private void setRightPane(javafx.scene.Node node) {
        RightPane.getChildren().clear();
        RightPane.getChildren().add(node);
    }

    public void addChatGroup(String Group) {
        Button button = new Button();
        button.setText(Group);
        button.setOnAction(e -> {
            SelectGroup(button.getText());
        });
        ChatGroups.put(Group, button);
        ChatGroupsFlowPane.getChildren().add(button);
    }

    public void deleteChatGroup(String Group) {
        Button button = ChatGroups.get(Group);
        if (button == null) return;
        ChatController chatController = MainJavaFXRunner.mainJavaFXRunner.Chat.getController();
        String BeInGroup = chatController.getModel();
        if (BeInGroup.equals(Group)) {
            String WillBeInGroup = "";
            if (ChatGroups.size() > 1) {
                WillBeInGroup = ChatGroups.firstKey();
                if (WillBeInGroup.equals(Group)) {
                    WillBeInGroup = ChatGroups.lastKey();
                }
            }
            chatController.setModel(WillBeInGroup);
        }
        ChatGroupsFlowPane.getChildren().remove(button);
        ChatGroups.remove(Group);
    }

    public void SelectGroup(String Group) {
        if (!containsChatGroup(Group)) return;
        ChatController chatController = MainJavaFXRunner.mainJavaFXRunner.Chat.getController();
        chatController.setModel(Group);
        setRightPane(MainJavaFXRunner.mainJavaFXRunner.ChatNode);
    }

    public boolean containsChatGroup(String Group) {
        return ChatGroups.keySet().contains(Group);
    }


    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static String markdownToHtmlExtensions(String markdown) {
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(tableExtension).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    public AttributeProvider create(AttributeProviderContext context) {
                        return new CustomAttributeProvider();
                    }
                })
                .build();
        return renderer.render(document);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainJavaFXRunner.mainJavaFXRunner.chatBySend = new ChatBySend();
        last = new StringBuffer();
        ChatGroups = new TreeMap<>();
    }

    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            if (node instanceof org.commonmark.node.Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof org.commonmark.ext.gfm.tables.TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }

}
