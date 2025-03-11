package com.timelordtty.spelling.controller;

import com.timelordtty.spelling.service.SpellCheckService;
import com.timelordtty.spelling.service.SpellCheckService.ErrorItem;
import com.timelordtty.spelling.ui.MainView;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tianyu.tang
 * @description UI控制器，处理界面事件和状态更新
 */
@Component
public class SpellCheckController {
    private static final Logger log = LoggerFactory.getLogger(SpellCheckController.class);
    
    private final SpellCheckService spellCheckService;
    private final ExecutorService executorService;
    
    // UI组件
    private TextArea inputArea;
    private TextFlow resultFlow;
    private TextArea correctedArea;
    private Label statusLabel;
    private Button checkButton;
    
    // 视图引用
    private MainView mainView;
    
    // 状态标志
    private volatile boolean isChecking = false;
    
    public SpellCheckController(SpellCheckService spellCheckService) {
        this.spellCheckService = spellCheckService;
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "spell-check-thread");
            thread.setDaemon(true);
            return thread;
        });
    }
    
    /**
     * 初始化UI组件引用
     * 用于旧版本兼容
     */
    public void initComponents(TextArea inputArea, TextFlow resultFlow, 
                             TextArea correctedArea, Label statusLabel, Button checkButton) {
        this.inputArea = inputArea;
        this.resultFlow = resultFlow;
        this.correctedArea = correctedArea;
        this.statusLabel = statusLabel;
        this.checkButton = checkButton;
        
        // 设置按钮事件
        checkButton.setOnAction(event -> startCheckProcess());
        
        // 创建一个临时的MainView来桥接
        this.mainView = new MainView() {
            @Override
            public TextArea getInputArea() { return inputArea; }
            @Override
            public TextFlow getResultFlow() { return resultFlow; }
            @Override
            public TextArea getCorrectedArea() { return correctedArea; }
            @Override
            public Label getStatusLabel() { return statusLabel; }
            @Override
            public Button getCheckButton() { return checkButton; }
        };
        
        // 启用UI
        enableUI();
    }
    
    /**
     * 初始化视图
     */
    public void initView(MainView mainView) {
        this.mainView = mainView;
        
        // 获取UI组件引用
        this.inputArea = mainView.getInputArea();
        this.resultFlow = mainView.getResultFlow();
        this.correctedArea = mainView.getCorrectedArea();
        this.statusLabel = mainView.getStatusLabel();
        this.checkButton = mainView.getCheckButton();
        
        // 设置按钮事件
        checkButton.setOnAction(event -> startCheckProcess());
        
        // 启用UI
        enableUI();
    }
    
    /**
     * 启用UI组件，完成初始化后调用
     */
    private void enableUI() {
        // 在初始化完成后执行
        executorService.submit(() -> {
            try {
                // 首先尝试初始化服务
                // 可能会初始化Token，这可能需要一些时间
                spellCheckService.checkText("测试连接");
                
                // 初始化成功，启用UI
                mainView.setLoading(false);
            } catch (Exception e) {
                log.error("初始化服务失败", e);
                mainView.showError("初始化服务失败: " + e.getMessage());
            }
        });
    }
    
    /**
     * 开始文本检查流程
     */
    public void startCheckProcess() {
        if (isChecking) {
            log.info("已有检查任务在执行中，忽略请求");
            return;
        }
        
        String text = inputArea.getText();
        if (text == null || text.trim().isEmpty()) {
            mainView.updateStatus("请输入要检查的文本", true);
            return;
        }
        
        // 更新UI状态
        isChecking = true;
        mainView.setLoading(true);
        
        // 清空结果区域
        Platform.runLater(() -> {
            resultFlow.getChildren().clear();
            correctedArea.setText("");
        });
        
        // 在后台线程执行API请求
        executorService.submit(() -> {
            try {
                // 发送请求
                String apiResponse = spellCheckService.checkText(text);
                
                // 处理响应
                processResponse(apiResponse);
            } catch (IOException e) {
                log.error("文本校验失败", e);
                mainView.showError("校验失败: " + e.getMessage());
            } finally {
                // 恢复UI状态
                isChecking = false;
                mainView.setLoading(false);
            }
        });
    }
    
    /**
     * 处理API响应
     */
    private void processResponse(String apiResponse) {
        if (apiResponse == null || apiResponse.isEmpty()) {
            mainView.updateStatus("校验失败: 服务器返回空响应", true);
            return;
        }
        
        try {
            // 解析错误项
            List<ErrorItem> errorItems = spellCheckService.getErrorItems(apiResponse);
            
            // 获取校正后的文本
            String correctedText = spellCheckService.getCorrectedText(apiResponse);
            
            // 更新UI
            updateUI(errorItems, correctedText);
            
            // 更新状态
            if (errorItems.isEmpty()) {
                mainView.updateStatus("文本检查完成：未发现错误", false);
            } else {
                mainView.updateStatus("文本检查完成：发现 " + errorItems.size() + " 处错误", false);
            }
        } catch (Exception e) {
            log.error("处理API响应失败", e);
            mainView.showError("处理结果失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新UI显示
     */
    private void updateUI(List<ErrorItem> errorItems, String correctedText) {
        Platform.runLater(() -> {
            // 更新错误流
            resultFlow.getChildren().clear();
            
            if (errorItems.isEmpty()) {
                Text noErrorText = new Text("文本校验通过，未发现拼写错误。");
                noErrorText.setFill(Color.GREEN);
                resultFlow.getChildren().add(noErrorText);
            } else {
                for (ErrorItem error : errorItems) {
                    Text errorInfo = new Text(error.toString() + "\n");
                    errorInfo.setFill(Color.RED);
                    resultFlow.getChildren().add(errorInfo);
                }
            }
            
            // 更新校正后文本
            if (correctedText != null && !correctedText.isEmpty()) {
                correctedArea.setText(correctedText);
            } else {
                correctedArea.setText("未能生成校正文本");
            }
        });
    }
    
    /**
     * 关闭资源
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 