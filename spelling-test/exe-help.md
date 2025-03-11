# 拼写检查工具构建与使用指南

## 构建说明

### 优化后的构建过程
现在的构建过程已经优化，只会生成必要的文件，包括：
- SpellCheckTool.exe - 可执行文件
- jre/ - 运行环境
- 必要的配置文件（logback-exe.xml, config.properties）
- logs/ - 日志目录

### 构建步骤
1. 执行Maven命令构建项目：
   ```
   mvn clean package
   ```

2. 构建完成后，所有必要文件将位于 `target` 目录下

## 使用说明

### 程序启动方式（三种均可）

1. **使用EXE文件启动**（推荐）
   - 直接双击 `SpellCheckTool.exe` 运行程序
   - 日志文件会自动保存到 `logs` 目录

2. **在IDE中启动**
   - 右键点击 `SpellCorrect.java` 文件并选择"运行"
   - 适合开发和测试场景

3. **使用Spring Boot面板启动**
   - 在IDE的Spring Boot面板中启动应用
   - 可以方便地查看控制台输出和管理应用生命周期

## 注意事项

1. 需要确保 JDK 版本 17 或更高
2. 所有配置文件和 EXE 文件必须位于同一目录
3. 首次运行时自动创建日志目录

## 故障排除

如果遇到 Token 初始化问题，请检查：
1. 百度 API 密钥配置是否正确
2. 网络连接是否正常
3. 查看 `logs/http.log` 中的详细错误信息

---
Copyright © 2024 TimelordTTY 