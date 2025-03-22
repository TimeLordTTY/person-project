package com.timelordtty.docgen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.timelordtty.docgen.service.DocGeneratorService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档生成器应用程序的主控制器
 */
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    private Stage stage;
    private DocGeneratorService docService;
    
    @FXML
    private ComboBox<String> templateTypeComboBox;
    
    @FXML
    private TextField templatePathField;
    
    @FXML
    private TextField outputPathField;
    
    @FXML
    private TextField dataFilePathField;
    
    @FXML
    private Button selectTemplateButton;
    
    @FXML
    private Button selectOutputDirButton;
    
    @FXML
    private Button selectDataFileButton;
    
    @FXML
    private Button generateButton;
    
    @FXML
    private Label statusLabel;

    /**
     * 初始化控制器
     */
    @FXML
    public void initialize() {
        docService = new DocGeneratorService();
        
        // 初始化模板类型下拉框
        templateTypeComboBox.getItems().addAll("Word文档(.docx)", "Excel表格(.xlsx)");
        templateTypeComboBox.getSelectionModel().selectFirst();
        
        // 设置状态标签初始文本
        statusLabel.setText("准备就绪");
    }

    /**
     * 设置主舞台引用
     * @param stage 主舞台
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * 选择模板文件按钮事件处理
     * @param event 事件对象
     */
    @FXML
    void onSelectTemplate(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择模板文件");
        
        // 根据选择的模板类型设置文件过滤器
        String selectedType = templateTypeComboBox.getValue();
        if (selectedType.contains("Word")) {
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Word文档", "*.docx")
            );
        } else if (selectedType.contains("Excel")) {
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel表格", "*.xlsx")
            );
        }
        
        // 显示文件选择对话框
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            templatePathField.setText(selectedFile.getAbsolutePath());
            statusLabel.setText("已选择模板文件: " + selectedFile.getName());
        }
    }

    /**
     * 选择输出目录按钮事件处理
     * @param event 事件对象
     */
    @FXML
    void onSelectOutputDir(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择输出目录");
        
        // 显示目录选择对话框
        File selectedDir = dirChooser.showDialog(stage);
        if (selectedDir != null) {
            outputPathField.setText(selectedDir.getAbsolutePath());
            statusLabel.setText("已选择输出目录: " + selectedDir.getName());
        }
    }

    /**
     * 选择数据文件按钮事件处理
     * @param event 事件对象
     */
    @FXML
    void onSelectDataFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据文件");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        // 显示文件选择对话框
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            dataFilePathField.setText(selectedFile.getAbsolutePath());
            statusLabel.setText("已选择数据文件: " + selectedFile.getName());
        }
    }

    /**
     * 生成文档按钮事件处理
     * @param event 事件对象
     */
    @FXML
    void onGenerateDocument(ActionEvent event) {
        // 验证是否所有必填字段都已填写
        if (templatePathField.getText().isEmpty() || 
            outputPathField.getText().isEmpty() || 
            dataFilePathField.getText().isEmpty()) {
            
            showAlert(Alert.AlertType.ERROR, "错误", "请填写所有字段", 
                      "模板文件、输出目录和数据文件均为必填项。");
            return;
        }
        
        try {
            // 获取选择的模板类型
            String templateType = templateTypeComboBox.getValue();
            boolean isWordTemplate = templateType.contains("Word");
            
            // 调用服务生成文档
            String templatePath = templatePathField.getText();
            String outputPath = outputPathField.getText();
            String dataFilePath = dataFilePathField.getText();
            
            String result = docService.generateDocument(
                templatePath, 
                outputPath, 
                dataFilePath, 
                isWordTemplate
            );
            
            // 显示成功消息
            statusLabel.setText("文档生成成功: " + result);
            showAlert(Alert.AlertType.INFORMATION, "成功", "文档生成成功", 
                      "文档已生成至: " + result);
            
        } catch (Exception e) {
            logger.error("生成文档时发生错误", e);
            statusLabel.setText("错误: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "错误", "生成文档失败", 
                      "原因: " + e.getMessage());
        }
    }
    
    /**
     * 显示警告对话框
     * @param type 警告类型
     * @param title 标题
     * @param header 头部信息
     * @param content 内容
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 