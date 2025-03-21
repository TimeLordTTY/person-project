@echo off
echo ===== 启动货币转换器应用程序 =====
echo.

set JAVA_HOME=D:\Soft\Java\jdk-23
set PATH=%JAVA_HOME%\bin;%PATH%

echo 使用 Java 版本:
java -version
echo.

echo 启动应用程序...
java -jar target\number-convert-1.0-SNAPSHOT-fat.jar
if %ERRORLEVEL% NEQ 0 (
    echo 运行应用程序时出错，错误代码: %ERRORLEVEL%
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo 应用程序已关闭。
echo. 