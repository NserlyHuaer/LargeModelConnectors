package com.nserly.Tools.Component;

import com.nserly.Logger;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.concurrent.*;

@Slf4j
public class EfficientMarkdownView extends StackPane {
    private final WebView webView = new WebView();
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    // 异步处理相关
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledUpdate;
    private volatile String latestMarkdown = "";

    public EfficientMarkdownView() {
        getChildren().add(webView);
        initializeWebViewStructure();
    }

    private void initializeWebViewStructure() {
        String initHTML = """
                <html>
                    <head>
                        <style>
                            body {
                                font-family: system-ui, -apple-system, sans-serif;
                                line-height: 1.6;
                                margin: 20px;
                            }
                            code {
                                font-family: Menlo, Monaco, Consolas,
                                "Courier New", monospace;
                                padding: 2px 4px;
                                background-color: #f3f4f4;
                                border-radius: 4px;
                            }
                            pre {
                                background-color: #f3f4f4;
                                padding: 10px;
                                border-radius: 6px;
                                overflow-x: auto;
                            }
                            blockquote {
                                border-left: 4px solid #dfe2e5;
                                padding: 0 15px;
                                color: #6a737d;
                                margin-left: 0;
                            }
                            table {
                                border-collapse: collapse;
                                margin: 1em 0;
                            }
                            th, td {
                                border: 1px solid #dfe2e5;
                                padding: 6px 13px;
                            }
                            th {
                                background-color: #f6f8fa;
                            }
                        </style>
                    </head>
                    <body id="content"></body>
                </html>
                """;
        webView.getEngine().loadContent(initHTML);
    }

    public void updateMarkdownAsync(String markdown) {
        latestMarkdown = markdown;

        // 取消之前未完成的更新任务
        if (scheduledUpdate != null && !scheduledUpdate.isDone()) {
            scheduledUpdate.cancel(false);
        }

        // 延迟合并更新请求
        scheduledUpdate = scheduler.schedule(() -> {
            executor.execute(() -> {
                try {
                    Node document = parser.parse(latestMarkdown);
                    String html = renderer.render(document);

                    Platform.runLater(() -> {
                        try {
                            webView.getEngine().executeScript(
                                    "document.getElementById('content').innerHTML = "
                                            + escapeJS(html) + ";"
                            );
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            log.info("Try re-rendering...");
                            initializeWebViewStructure();
                            updateMarkdownAsync(markdown);
                            log.info("Completed re-rendering!");

                        }
                    });
                } catch (Exception e) {
                    log.error(Logger.getExceptionMessage(e));
                    initializeWebViewStructure();
                    updateMarkdownAsync(markdown);
                }
            });
        }, 50, TimeUnit.MILLISECONDS); // 50ms延迟窗口期
    }

    private String escapeJS(String content) {
        return "'" + content.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r") + "'";
    }

    public void bindToTextProperty(javafx.beans.property.StringProperty prop) {
        prop.addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(oldVal)) {
                updateMarkdownAsync(newVal);
            }
        });
    }

    public void close() throws Throwable {
        executor.shutdown();
        scheduler.shutdown();
    }

    public void setMarkdown(String markdown) {
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        String fullHtml = "<html><body>" + html + "</body></html>";
        webView.getEngine().loadContent(fullHtml);
    }

}