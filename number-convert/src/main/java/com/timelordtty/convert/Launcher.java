package com.timelordtty.convert;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用程序启动器类
 * 负责设置JavaFX模块路径并启动主应用程序
 */
public class Launcher {

    public static void main(String[] args) {
        try {
            // 检查JavaFX是否已经可用
            try {
                Class.forName("javafx.application.Application");
                // JavaFX已经在类路径中可用，直接启动应用程序
                MoneyConverterApp.main(args);
                return;
            } catch (ClassNotFoundException e) {
                // JavaFX不在类路径中，需要手动添加
                System.out.println("JavaFX不在类路径中，尝试手动添加...");
            }

            // 尝试查找JavaFX模块
            File appDir = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File libDir = new File(appDir, "lib");
            if (!libDir.exists()) {
                // 尝试父目录的lib目录
                libDir = new File(appDir.getParentFile(), "lib");
            }

            if (!libDir.exists()) {
                System.err.println("无法找到JavaFX库目录！");
                return;
            }

            // 查找所有JavaFX JAR文件
            List<URL> jfxJars = new ArrayList<>();
            File[] files = libDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("javafx-") && file.getName().endsWith(".jar")) {
                        jfxJars.add(file.toURI().toURL());
                        System.out.println("添加JavaFX模块: " + file.getName());
                    }
                }
            }

            if (jfxJars.isEmpty()) {
                System.err.println("未找到JavaFX JAR文件！");
                return;
            }

            // 创建新的类加载器
            URLClassLoader classLoader = new URLClassLoader(
                    jfxJars.toArray(new URL[0]),
                    Launcher.class.getClassLoader()
            );

            // 加载并启动主应用程序
            Thread.currentThread().setContextClassLoader(classLoader);

            // 设置JavaFX模块路径
            String modulePath = libDir.getAbsolutePath();
            System.setProperty("javafx.modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.base");
            System.setProperty("javafx.modulepath", modulePath);

            // 使用反射加载并调用主类的main方法
            Class<?> mainClass = Class.forName("com.timelordtty.convert.MoneyConverterApp", true, classLoader);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);

        } catch (Exception e) {
            System.err.println("启动应用程序时出错: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 