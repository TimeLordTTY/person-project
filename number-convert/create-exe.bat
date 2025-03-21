@echo off
echo ===== 创建独立可执行EXE程序 =====

set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo 1. 清理并打包项目...
call ..\mvn-jdk23.cmd clean package
if %ERRORLEVEL% neq 0 goto error

echo 2. 创建EXE文件...
jpackage ^
  --type exe ^
  --name "金额转换工具" ^
  --app-version "1.0.0" ^
  --vendor "Example" ^
  --description "金额数字大小写转换工具" ^
  --input target ^
  --main-jar number-convert-1.0-SNAPSHOT-fat.jar ^
  --main-class com.example.Launcher ^
  --dest target\exe ^
  --java-options "--add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base" ^
  --win-console ^
  --win-shortcut

if %ERRORLEVEL% neq 0 goto error

echo 完成！EXE文件已创建在 target\exe 目录中
goto :eof

:error
echo 构建过程中出错，错误代码: %ERRORLEVEL%
exit /b %ERRORLEVEL% 