package com.timelordtty.spelling;

import com.timelordtty.spelling.controller.SpellCheckController;
import com.timelordtty.spelling.ui.MainView;
import com.timelordtty.spelling.utils.JavaFxUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;

/**
 * @author tianyu.tang
 * @description 应用程序主类，负责启动Spring容器和JavaFX应用程序
 * 
 * 这个类承担两个主要职责：
 * 1. 作为Spring Boot应用的入口点
 * 2. 作为JavaFX应用的入口点
 * 
 * 它负责将这两个框架整合在一起，但不包含具体业务逻辑和UI构建代码。
 */
@SpringBootApplication
public class SpellCorrect extends Application {
    private static final Logger log = LoggerFactory.getLogger(SpellCorrect.class);
    
    /**
     * Spring应用上下文，用于访问Bean和管理生命周期
     */
    private static ConfigurableApplicationContext springContext;
    
    /**
     * 主视图组件引用
     */
    private MainView mainView;
    
    /**
     * JavaFX应用程序初始化方法
     * 负责创建和配置Spring上下文
     */
    @Override
    public void init() {
        try {
            log.info("初始化Spring上下文");
            springContext = SpringApplication.run(SpellCorrect.class);
            log.info("Spring上下文初始化完成");
        } catch (Exception e) {
            log.error("初始化Spring上下文失败", e);
            throw new RuntimeException("初始化Spring上下文失败", e);
        }
    }
    
    /**
     * JavaFX应用程序停止方法
     * 负责清理资源和关闭Spring上下文
     */
    @Override
    public void stop() throws Exception {
        log.info("正在关闭应用...");
        
        // 关闭控制器资源
        try {
            if (springContext != null) {
                SpellCheckController controller = springContext.getBean(SpellCheckController.class);
                controller.shutdown();
            }
        } catch (Exception e) {
            log.warn("关闭控制器资源失败", e);
        }
        
        // 关闭Spring上下文
        if (springContext != null) {
            springContext.close();
            log.info("Spring上下文已关闭");
        }
        
        super.stop();
    }
    
    /**
     * JavaFX应用程序启动方法
     * 负责创建UI并显示主窗口
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            log.info("开始构建UI");
            
            // 创建主视图
            mainView = new MainView();
            mainView.initialize(primaryStage);
            
            // 获取UI组件并初始化控制器
            if (springContext != null) {
                SpellCheckController controller = springContext.getBean(SpellCheckController.class);
                controller.initView(mainView);
            }
            
            // 显示主窗口
            primaryStage.show();
            log.info("UI初始化完成");
            
        } catch (Exception e) {
            log.error("启动UI失败", e);
            Platform.exit();
        }
    }
    
    /**
     * 应用程序主入口点
     * 负责设置环境和启动JavaFX应用
     */
    public static void main(String[] args) {
        try {
            // 设置控制台编码为UTF-8
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("sun.jnu.encoding", "UTF-8");
            
            // 创建日志目录
            JavaFxUtils.checkAndCreateLogDir();
            
            // 设置日志配置文件位置
            File logbackFile = JavaFxUtils.findLogbackConfig();
            if (logbackFile != null) {
                System.setProperty("logback.configurationFile", logbackFile.getAbsolutePath());
            }
            
            // 一旦日志系统初始化完成，使用日志输出
            log.info("当前系统编码: {}", System.getProperty("file.encoding"));
            log.info("当前工作目录: {}", System.getProperty("user.dir"));
            
            // 检测当前运行环境
            boolean isExeEnv = JavaFxUtils.detectRunningEnvironment();
            log.info("当前运行环境: {}", isExeEnv ? "EXE/JAR" : "IDE/开发");
            
            // IDE环境特殊处理 - 尝试加载JavaFX
            if (!isExeEnv) {
                try {
                    log.info("尝试直接从类路径加载JavaFX...");
                    Class.forName("javafx.application.Application");
                    log.info("JavaFX可以直接从类路径加载，继续启动...");
                } catch (ClassNotFoundException e) {
                    log.warn("找不到JavaFX类，尝试设置模块路径...");
                    if (!JavaFxUtils.setupJavaFxModulePath()) {
                        log.error("无法加载JavaFX模块。请确保添加了--module-path参数或将JavaFX依赖添加到类路径中。");
                        throw new RuntimeException("无法加载JavaFX", e);
                    }
                }
            }
            
            // 启动JavaFX应用程序
            log.info("开始启动JavaFX应用程序...");
            launch(args);
        } catch (Throwable e) {
            log.error("启动失败: {}", e.getMessage(), e);
            
            // 详细诊断信息
            log.error("===== 诊断信息 =====");
            log.error("Java版本: {}", System.getProperty("java.version"));
            log.error("工作目录: {}", System.getProperty("user.dir"));
            log.error("类路径: {}", System.getProperty("java.class.path"));
            log.error("模块路径: {}", System.getProperty("jdk.module.path", "未设置"));
            
            System.exit(1);
        }
    }
}
