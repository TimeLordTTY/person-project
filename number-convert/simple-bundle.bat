@echo off
echo ===== 创建便携式应用程序包 =====
echo 此脚本将创建一个可以在任何Windows电脑上运行的便携版应用程序

set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo 1. 清理并打包项目...
call ..\mvn-jdk23.cmd clean package
if %ERRORLEVEL% neq 0 goto error

echo 2. 创建便携版目录...
rmdir /s /q portable 2>nul
mkdir portable
mkdir portable\lib

echo 3. 复制应用JAR文件...
copy /Y target\number-convert-1.0-SNAPSHOT-fat.jar portable\MoneyConverter.jar
if %ERRORLEVEL% neq 0 goto error

echo 4. 复制JavaFX库...
for %%f in (target\lib\javafx-*.jar) do (
  copy /Y "%%f" portable\lib\
)
if %ERRORLEVEL% neq 0 goto error

echo 5. 创建便携版JRE...
jlink --module-path "%JAVA_HOME%\jmods" --add-modules java.base,java.desktop,java.naming,java.sql,java.xml,jdk.unsupported --output portable\jre --strip-debug --no-header-files --no-man-pages --compress=2
if %ERRORLEVEL% neq 0 goto error

echo 6. 创建启动脚本...
(
echo @echo off
echo setlocal
echo set "APP_HOME=%%~dp0"
echo echo 正在启动金额转换工具...
echo "%%APP_HOME%%jre\bin\java" --module-path "%%APP_HOME%%lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "%%APP_HOME%%MoneyConverter.jar"
echo if %%ERRORLEVEL%% neq 0 ^(
echo   echo 程序异常退出，错误代码: %%ERRORLEVEL%%
echo   pause
echo ^)
) > portable\金额转换工具.bat

echo 7. 创建说明文件...
(
echo 金额转换工具 - 便携版
echo ====================
echo.
echo 使用方法:
echo 双击 "金额转换工具.bat" 文件启动应用程序
echo.
echo 本程序是完全独立的便携版，不需要安装Java或其他组件。
) > portable\使用说明.txt

echo 完成！便携式应用程序已创建在portable目录中
echo 您可以将整个portable目录复制到任何Windows电脑上运行
goto :eof

:error
echo 构建过程中出错，错误代码: %ERRORLEVEL%
exit /b %ERRORLEVEL% 