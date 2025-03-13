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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        // 检查是否已设置JavaFX模块路径
        if (!hasJavaFxModulePath()) {
            // 尝试自动添加JavaFX模块路径并重启进程
            boolean restarted = restartWithJavaFxModules(args);
            if (restarted) {
                // 如果重启成功，当前进程将退出，不会执行下面的代码
                return;
            }
        }
        
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
            System.out.println("当前系统编码: " + System.getProperty("file.encoding"));
            System.out.println("当前工作目录: " + System.getProperty("user.dir"));
            
            // 检测当前运行环境
            boolean isExeEnv = JavaFxUtils.detectRunningEnvironment();
            System.out.println("当前运行环境: " + (isExeEnv ? "EXE/JAR" : "IDE/开发"));
            
            // IDE环境特殊处理 - 尝试加载JavaFX
            if (!isExeEnv) {
                try {
                    System.out.println("尝试直接从类路径加载JavaFX...");
                    Class.forName("javafx.application.Application");
                    System.out.println("JavaFX可以直接从类路径加载，继续启动...");
                } catch (ClassNotFoundException e) {
                    System.err.println("警告: 找不到JavaFX类，尝试设置模块路径...");
                    if (!JavaFxUtils.setupJavaFxModulePath()) {
                        System.err.println("无法加载JavaFX模块。请确保添加了--module-path参数或将JavaFX依赖添加到类路径中。");
                        
                        // 最后尝试一次重启
                        boolean restarted = restartWithJavaFxModules(args);
                        if (!restarted) {
                            throw new RuntimeException("无法加载JavaFX", e);
                        }
                        return;
                    }
                }
            }
            
            // 启动JavaFX应用程序
            System.out.println("开始启动JavaFX应用程序...");
            launch(args);
        } catch (Throwable e) {
            System.err.println("启动失败: " + e.getMessage());
            e.printStackTrace();
            
            // 详细诊断信息
            System.err.println("\n===== 诊断信息 =====");
            System.err.println("Java版本: " + System.getProperty("java.version"));
            System.err.println("工作目录: " + System.getProperty("user.dir"));
            System.err.println("类路径: " + System.getProperty("java.class.path"));
            System.err.println("模块路径: " + System.getProperty("jdk.module.path", "未设置"));
            
            System.exit(1);
        }
    }
    
    /**
     * 检查是否已设置JavaFX模块路径
     */
    private static boolean hasJavaFxModulePath() {
        // 检查命令行或系统属性中是否有模块路径设置
        String jdkModulePath = System.getProperty("jdk.module.path");
        String javafxModulePath = System.getProperty("javafx.module.path");
        String commandLine = System.getProperty("sun.java.command", "");
        
        return jdkModulePath != null || javafxModulePath != null || 
               commandLine.contains("--module-path") || 
               commandLine.contains("--add-modules");
    }
    
    /**
     * 使用JavaFX模块路径重启进程
     * 
     * @param originalArgs 原始命令行参数
     * @return 是否成功重启
     */
    private static boolean restartWithJavaFxModules(String[] originalArgs) {
        try {
            // 获取Java可执行文件路径
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                javaBin += ".exe";
            }
            
            // 查找JavaFX模块路径
            String javaFxPath = null;
            String[] potentialPaths = {
                "target/dependency",
                System.getProperty("user.home") + "/.m2/repository/org/openjfx",
                "spelling-test/target/dependency",
                "lib/javafx-modules",
                "javafx-sdk/lib"
            };
            
            for (String path : potentialPaths) {
                File dir = new File(path);
                if (dir.exists() && dir.isDirectory()) {
                    // 检查是否包含JavaFX JAR
                    File[] files = dir.listFiles((d, name) -> 
                        name.endsWith(".jar") && (name.contains("javafx") || name.contains("openjfx"))
                    );
                    
                    if (files != null && files.length > 0) {
                        javaFxPath = dir.getAbsolutePath();
                        System.out.println("找到JavaFX模块路径: " + javaFxPath);
                        break;
                    }
                }
            }
            
            // 找不到JavaFX模块路径，使用默认值
            if (javaFxPath == null) {
                javaFxPath = "target/dependency";
                System.out.println("使用默认JavaFX模块路径: " + javaFxPath);
                // 创建目录确保存在
                new File(javaFxPath).mkdirs();
            }
            
            // 构建命令行参数
            List<String> command = new ArrayList<>();
            command.add(javaBin);
            
            // 添加原始的JVM参数（如果有）
            String jvmArgs = System.getProperty("sun.java.command", "");
            if (jvmArgs.contains(" -")) {
                String[] parts = jvmArgs.split(" -");
                for (int i = 1; i < parts.length; i++) {
                    command.add("-" + parts[i].trim());
                }
            }
            
            // 添加JavaFX模块参数
            command.add("--module-path=" + javaFxPath);
            command.add("--add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics");
            
            // 添加其他必要参数
            command.add("--enable-preview");
            command.add("-Dfile.encoding=UTF-8");
            
            // 添加开放模块参数
            command.add("--add-opens=java.base/java.lang=ALL-UNNAMED");
            command.add("--add-opens=java.base/java.io=ALL-UNNAMED");
            command.add("--add-opens=java.base/java.util=ALL-UNNAMED");
            command.add("--add-opens=java.base/java.util.concurrent=ALL-UNNAMED");
            command.add("--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED");
            
            // 添加类路径
            command.add("-cp");
            command.add(System.getProperty("java.class.path"));
            
            // 添加主类
            command.add(SpellCorrect.class.getName());
            
            // 添加原始参数
            command.addAll(Arrays.asList(originalArgs));
            
            // 输出将要执行的命令（调试用）
            System.out.println("正在使用JavaFX模块路径重启: " + String.join(" ", command));
            
            // 启动新进程
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            
            // 等待进程启动
            Thread.sleep(500);
            
            // 如果新进程已经开始，退出当前进程
            System.exit(0);
            return true;
        } catch (IOException | InterruptedException e) {
            System.err.println("重启进程失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
