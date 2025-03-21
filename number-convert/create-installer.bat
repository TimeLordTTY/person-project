@echo off
echo ===== 创建Windows安装程序 =====
echo 此脚本将创建一个Windows安装程序，包含所有需要的JavaFX和JRE组件

set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo 1. 清理并打包项目...
call ..\mvn-jdk23.cmd clean package
if %ERRORLEVEL% neq 0 goto error

echo 2. 创建应用程序映像...
jpackage --type app-image ^
  --name "金额转换工具" ^
  --app-version "1.0.0" ^
  --vendor "Example" ^
  --description "金额数字大小写转换工具" ^
  --copyright "Copyright © 2025" ^
  --input target ^
  --main-jar 金额转换工具-fat.jar ^
  --main-class com.example.NumberConverterApp ^
  --dest target\jpackage ^
  --java-options "--module-path lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base" ^
  --runtime-image "%JAVA_HOME%"
if %ERRORLEVEL% neq 0 goto error

echo 3. 创建Windows安装程序...
jpackage --type exe ^
  --app-image target\jpackage\金额转换工具 ^
  --name "金额转换工具" ^
  --dest target\installer ^
  --app-version "1.0.0" ^
  --vendor "Example" ^
  --win-dir-chooser ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group "金额转换工具" ^
  --resource-dir target\jpackage\金额转换工具
if %ERRORLEVEL% neq 0 goto error

echo 完成！安装程序已创建在 target\installer 目录中
goto :eof

:error
echo 构建过程中出错，错误代码: %ERRORLEVEL%
exit /b %ERRORLEVEL% 