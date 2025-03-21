@echo off
echo ===== 运行便携式货币转换器应用程序 =====

cd portable
echo 当前目录: %CD%
echo.

echo 使用 Java 版本:
java -version
echo.

echo 启动应用程序...
java -jar MoneyConverter.jar
if %ERRORLEVEL% NEQ 0 (
    echo 运行应用程序时出错，错误代码: %ERRORLEVEL%
    pause
)

cd ..
echo.
echo 应用程序已关闭。
echo. 