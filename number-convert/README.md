# 货币转换器

一个简单的货币数字大小写转换工具，可以转换人民币金额的大小写。

## 项目结构

- `src/main/java`: Java源代码目录
- `src/main/resources`: 资源文件目录
- `scripts`: 脚本目录，包含打包和运行脚本
- `launch4j-config.xml`: Launch4j配置文件，用于创建EXE文件

## 开发环境

- JDK 23
- Maven 3.9.9
- JavaFX 17.0.2

## 使用方法

### 运行应用程序

如果您已经安装了Java，可以直接使用以下命令运行应用程序：

```
scripts/run-app.bat
```

或者使用Maven运行：

```
mvn clean package
java -jar target/MoneyConverter-fat.jar
```

### 创建可执行文件

要创建一个可在没有Java环境的电脑上运行的EXE文件，请运行：

```
scripts/create-exe.bat
```

该脚本将创建一个包含JRE的独立可执行程序，位于`dist`目录中。您可以将整个`dist`目录复制到任何电脑上运行，无需安装Java环境。

## 注意事项

- 此应用程序需要Windows操作系统
- 创建EXE文件需要Internet连接（首次运行时下载Launch4j）

## 功能特点

- 支持将数字金额转换为中文大写形式
- 支持到亿级别的转换
- 支持小数点后两位（角、分）
- 正确处理零和单位的显示
- 美观的用户界面

## 技术实现

- 使用JavaFX开发界面
- 使用Maven进行项目管理和构建
- 使用Launch4j插件创建Windows可执行文件 