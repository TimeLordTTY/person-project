package com.timelordtty.spelling.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tianyu.tang
 * @description 主视图类，负责创建和管理UI组件
 * 
 * 这个类遵循单一职责原则，只负责UI构建和管理，
 * 不包含任何业务逻辑和事件处理代码。
 */
public class MainView {
    private static final Logger log = LoggerFactory.getLogger(MainView.class);
    
    // UI组件
    private TextArea inputArea;
    private TextFlow resultFlow;
    private TextArea correctedArea;
    private Label statusLabel;
    private Button checkButton;
    
    /**
     * 获取输入文本区域
     */
    public TextArea getInputArea() {
        return inputArea;
    }
    
    /**
     * 获取结果文本流
     */
    public TextFlow getResultFlow() {
        return resultFlow;
    }
    
    /**
     * 获取校正后文本区域
     */
    public TextArea getCorrectedArea() {
        return correctedArea;
    }
    
    /**
     * 获取状态标签
     */
    public Label getStatusLabel() {
        return statusLabel;
    }
    
    /**
     * 获取校验按钮
     */
    public Button getCheckButton() {
        return checkButton;
    }
    
    /**
     * 更新状态标签
     * 
     * @param message 状态消息
     * @param isError 是否为错误消息
     */
    public void updateStatus(String message, boolean isError) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText(message);
            if (isError) {
                statusLabel.setStyle("-fx-text-fill: red;");
            } else {
                statusLabel.setStyle("-fx-text-fill: black;");
            }
        });
    }

    /**
     * 设置面板加载状态
     * 
     * @param loading 是否正在加载
     */
    public void setLoading(boolean loading) {
        javafx.application.Platform.runLater(() -> {
            if (loading) {
                checkButton.setDisable(true);
                updateStatus("正在初始化，请稍候...", false);
            } else {
                checkButton.setDisable(false);
                updateStatus("初始化完成，可以开始使用", false);
            }
        });
    }

    /**
     * 显示错误消息
     * 
     * @param errorMessage 错误消息
     */
    public void showError(String errorMessage) {
        javafx.application.Platform.runLater(() -> {
            updateStatus("发生错误: " + errorMessage, true);
            
            // 在结果区域显示错误
            resultFlow.getChildren().clear();
            javafx.scene.text.Text errorText = new javafx.scene.text.Text(errorMessage);
            errorText.setFill(javafx.scene.paint.Color.RED);
            resultFlow.getChildren().add(errorText);
        });
    }
    
    /**
     * 初始化并构建UI
     * 
     * @param stage JavaFX主舞台
     */
    public void initialize(Stage stage) {
        log.info("开始构建UI组件");
        
        // 创建布局容器
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        // 创建输入文本区域
        inputArea = new TextArea();
        inputArea.setPrefSize(800, 400);
        inputArea.setPromptText("请在此粘贴或输入文章...");
        
        // 创建结果显示区域
        resultFlow = new TextFlow();
        resultFlow.setPrefSize(800, 200);
        resultFlow.setPadding(new Insets(10));
        ScrollPane resultScroll = new ScrollPane(resultFlow);
        resultScroll.setFitToWidth(true);
        
        // 创建校正后文本区域
        correctedArea = new TextArea();
        correctedArea.setPrefSize(800, 150);
        correctedArea.setEditable(false);
        correctedArea.setWrapText(true);
        
        // 创建状态标签
        statusLabel = new Label("正在初始化，请稍候...");
        statusLabel.setMinHeight(30);  // 确保标签有足够高度显示
        
        // 创建校验按钮
        checkButton = new Button("校验文本");
        checkButton.setPrefWidth(150);
        checkButton.setDisable(true);  // 初始状态禁用按钮
        
        // 组装UI组件
        root.getChildren().addAll(
            new Label("输入文本:"),
            inputArea,
            new Label("错误列表:"),
            resultScroll,
            new Label("校正后文本:"),
            correctedArea,
            checkButton,
            statusLabel
        );
        
        // 设置场景
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        // 设置窗口标题和大小
        stage.setTitle("文本拼写检查工具");
        stage.setWidth(820);
        stage.setHeight(850);
        
        // 添加关闭窗口处理
        stage.setOnCloseRequest(event -> {
            log.info("用户关闭窗口");
        });
        
        log.info("UI组件构建完成");
    }
} 