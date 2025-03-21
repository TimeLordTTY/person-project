package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NumberConverterApp extends Application {

    private TextField inputField;
    private TextField resultTextField;
    private Label digitalResultLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("金额数字大小写转换工具");

        // 创建主布局
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // 标题
        Text titleText = new Text("金额数字大小写转换工具");
        titleText.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 20));
        titleText.setFill(Color.web("#333333"));
        
        // 创建输入区域
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(15);
        inputGrid.setVgap(15);
        inputGrid.setAlignment(Pos.CENTER);

        Label inputLabel = new Label("请输入金额（元）：");
        inputLabel.setFont(Font.font("Microsoft YaHei", 14));
        
        inputField = new TextField();
        inputField.setPromptText("例如：199900.00");
        inputField.setPrefWidth(250);
        inputField.setPrefHeight(35);
        inputField.setFont(Font.font("Microsoft YaHei", 14));
        inputField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        // 按钮区域
        Button convertButton = new Button("转换");
        convertButton.setPrefWidth(100);
        convertButton.setPrefHeight(35);
        convertButton.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        convertButton.setStyle(
            "-fx-background-color: #4285f4; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );
        convertButton.setOnAction(e -> convertNumber());

        Button clearButton = new Button("清除");
        clearButton.setPrefWidth(100);
        clearButton.setPrefHeight(35);
        clearButton.setFont(Font.font("Microsoft YaHei", 14));
        clearButton.setStyle(
            "-fx-background-color: #f1f1f1; " +
            "-fx-text-fill: #333333; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );
        clearButton.setOnAction(e -> {
            inputField.clear();
            resultTextField.setText("");
            digitalResultLabel.setText("");
        });

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(convertButton, clearButton);

        // 创建结果显示区域
        VBox resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER_LEFT);
        resultBox.setPadding(new Insets(20));
        resultBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );
        
        Label resultTitleLabel = new Label("转换结果：");
        resultTitleLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        
        digitalResultLabel = new Label();
        digitalResultLabel.setFont(Font.font("Microsoft YaHei", 14));
        digitalResultLabel.setWrapText(true);
        
        resultTextField = new TextField();
        resultTextField.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        resultTextField.setEditable(false);
        resultTextField.setPrefWidth(450);
        resultTextField.setStyle(
            "-fx-text-fill: #4285f4; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: transparent; " + 
            "-fx-padding: 0; " +
            "-fx-border-width: 0; " +
            "-fx-highlight-fill: #d9e8fb; " +
            "-fx-highlight-text-fill: #4285f4;"
        );
        
        resultBox.getChildren().addAll(resultTitleLabel, digitalResultLabel, resultTextField);
        resultBox.setVisible(false);

        // 添加组件到布局
        inputGrid.add(inputLabel, 0, 0);
        inputGrid.add(inputField, 1, 0);
        
        mainLayout.getChildren().addAll(
            titleText,
            new Separator(),
            inputGrid,
            buttonBox,
            resultBox
        );

        // 创建场景
        Scene scene = new Scene(mainLayout, 550, 450);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void convertNumber() {
        try {
            VBox resultBox = (VBox) digitalResultLabel.getParent();
            
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                showError("请输入金额！");
                resultBox.setVisible(true);
                return;
            }

            // 检查输入格式
            if (!input.matches("^\\d+(\\.\\d{0,2})?$")) {
                showError("请输入正确的金额格式！\n例如：199900.00");
                resultBox.setVisible(true);
                return;
            }

            // 转换金额
            double amount = Double.parseDouble(input);
            String chineseAmount = NumberConverter.convert(amount);
            
            // 显示结果
            digitalResultLabel.setText(String.format("小写金额：%,.2f元", amount));
            digitalResultLabel.setTextFill(Color.BLACK);
            digitalResultLabel.setStyle("-fx-font-size: 14px;");
            
            resultTextField.setText(String.format("大写金额：%s", chineseAmount));
            resultTextField.setStyle(
                "-fx-text-fill: #4285f4; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: transparent; " +
                "-fx-padding: 0; " +
                "-fx-border-width: 0; " +
                "-fx-highlight-fill: #d9e8fb; " +
                "-fx-highlight-text-fill: #4285f4;"
            );
            
            resultBox.setVisible(true);
        } catch (Exception e) {
            showError("转换出错：" + e.getMessage());
        }
    }

    private void showError(String message) {
        resultTextField.setText("错误：" + message);
        resultTextField.setStyle(
            "-fx-text-fill: red; " + 
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: transparent; " +
            "-fx-padding: 0; " +
            "-fx-border-width: 0;"
        );
        digitalResultLabel.setText("");
    }

    public static void main(String[] args) {
        launch(args);
    }
} 