package com.timelordtty.docgen;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 文档生成器应用程序入口类
 */
public class DocGeneratorApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = loader.load();
        
        // 获取控制器实例并设置主舞台
        MainController controller = loader.getController();
        controller.setStage(primaryStage);
        
        // 设置场景和标题
        Scene scene = new Scene(root);
        primaryStage.setTitle("文档生成器");
        primaryStage.setScene(scene);
        
        // 尝试加载应用图标
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app_icon.png")));
        } catch (Exception e) {
            System.err.println("无法加载应用图标: " + e.getMessage());
        }
        
        // 显示主窗口
        primaryStage.show();
    }

    /**
     * 应用程序主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("文档生成器应用程序启动中...");
        System.out.println("Java版本: " + System.getProperty("java.version"));
        System.out.println("JavaFX版本: " + System.getProperty("javafx.version", "未知"));
        
        try {
            // 检查JavaFX模块
            Class.forName("javafx.application.Application");
            System.out.println("JavaFX模块检查通过");
            
            // 启动JavaFX应用程序
            launch(args);
        } catch (ClassNotFoundException e) {
            System.err.println("错误: 找不到JavaFX类。请确保JavaFX模块可用。");
            System.err.println("错误详情: " + e.getMessage());
            System.err.println("尝试使用以下命令行参数来运行应用程序:");
            System.err.println("java --enable-preview --module-path <javafx目录> --add-modules=javafx.controls,javafx.fxml -jar <应用程序jar文件>");
        } catch (Exception e) {
            System.err.println("启动应用程序时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 应用程序退出前的清理工作
     */
    @Override
    public void stop() {
        System.out.println("应用程序正在关闭...");
        // 在这里执行清理工作（如有必要）
    }
} 