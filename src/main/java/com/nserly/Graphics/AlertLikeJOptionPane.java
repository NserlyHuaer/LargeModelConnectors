package com.nserly.Graphics;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * 此类由于作者以前常常用swing做GUI，后来开始做Javafx后，对javafx消息对话框很不满，于是开发了此类
 * <p>
 * 此类可像JOptionPane一样的创建消息对话框窗体，方便熟悉swing但不怎么会javafx的人快速适应
 * </p>
 */
public class AlertLikeJOptionPane extends Alert {

    /*
    Option types
    */
    public static final int DEFAULT_OPTION = 1;
    public static final int YES_NO_OPTION = 2;
    public static final int YES_NO_CANCEL_OPTION = 3;
    public static final int OK_CANCEL_OPTION = 4;

    /*
    Return values
    */
    public static final int YES_OPTION = 0;
    public static final int NO_OPTION = 1;
    public static final int CANCEL_OPTION = 2;
    public static final int OK_OPTION = 0;
    public static final int CLOSED_OPTION = 3;

    // 定义消息类型常量
    public static final int INFORMATION_MESSAGE = 10;
    public static final int WARNING_MESSAGE = 11;
    public static final int ERROR_MESSAGE = 12;

    public AlertLikeJOptionPane(AlertType alertType) {
        super(alertType);
    }

    /**
     *
     *
     * @param HeaderText           头部信息
     * @param ContentText          正文信息
     * @param title                标题
     * @return 此类（一般初始化后）
     */

    public static int showConfirmDialog(Stage ParentStage, String HeaderText, String ContentText, String title, int optionType) {
        AlertLikeJOptionPane alertLikeJOptionPane = new AlertLikeJOptionPane(AlertType.INFORMATION);
        alertLikeJOptionPane.setTitle(title);
        alertLikeJOptionPane.setHeaderText(HeaderText);
        alertLikeJOptionPane.setHeaderText(ContentText);
        // 根据 optionType 配置按钮
        switch (optionType) {
            case YES_NO_OPTION:
                alertLikeJOptionPane.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                break;
            case YES_NO_CANCEL_OPTION:
                alertLikeJOptionPane.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                break;
            case YES_OPTION:
                alertLikeJOptionPane.getButtonTypes().setAll(ButtonType.YES);
                break;
            default:
                alertLikeJOptionPane.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL); // 默认配置
        }

        // 绑定父窗口（如果存在）
        if (ParentStage != null) {
            alertLikeJOptionPane.initOwner(ParentStage);
            alertLikeJOptionPane.initModality(Modality.WINDOW_MODAL); // 阻塞父窗口
        }

        // 显示对话框并获取结果
        Optional<ButtonType> result = alertLikeJOptionPane.showAndWait();

        // 将结果转换为 int 返回值
        return result.map(buttonType -> {
            if (buttonType == ButtonType.YES) {
                return YES_OPTION;
            } else if (buttonType == ButtonType.NO) {
                return NO_OPTION;
            } else if (buttonType == ButtonType.CANCEL) {
                return CANCEL_OPTION;
            } else {
                return CLOSED_OPTION; // 默认或未处理的情况
            }
        }).orElse(CLOSED_OPTION); // 用户直接关闭对话框时视为取消
    }

    public static void showMessageDialog(Stage parentStage, String headerText, String contentText, String title, int messageType) {
        AlertLikeJOptionPane alertLikeJOptionPane = null;
        switch (messageType) {
            case WARNING_MESSAGE -> alertLikeJOptionPane = new AlertLikeJOptionPane(AlertType.WARNING);
            case ERROR_MESSAGE -> alertLikeJOptionPane = new AlertLikeJOptionPane(AlertType.ERROR);
            default -> alertLikeJOptionPane = new AlertLikeJOptionPane(AlertType.INFORMATION);
        }
        alertLikeJOptionPane.setTitle(title);
        alertLikeJOptionPane.setHeaderText(headerText);
        alertLikeJOptionPane.setHeaderText(contentText);
        // 绑定父窗口
        if (parentStage != null) {
            alertLikeJOptionPane.initOwner(parentStage);
            alertLikeJOptionPane.initModality(Modality.WINDOW_MODAL);
        }
        // 显示对话框（无需返回结果）
        alertLikeJOptionPane.showAndWait();
    }

    public static String showInputDialog(Stage parentStage, String headerText, String contentText, String title, String defaultValue) {
        // 创建输入对话框
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        // 绑定父窗口
        if (parentStage != null) {
            dialog.initOwner(parentStage);
            dialog.initModality(Modality.WINDOW_MODAL);
        }
        // 显示对话框并获取结果
        Optional<String> result = dialog.showAndWait();
        // 返回输入值（用户取消时返回 null）
        return result.orElse(null);
    }


    /**
     * 获取组件所在的Stage
     *
     * @param node 组件
     * @return Stage
     */
    public static Stage getStage(Node node) {
        return (Stage) node.getScene().getWindow();
    }
}
