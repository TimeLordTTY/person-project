@echo off
echo ===== 创建便携式应用程序 =====
echo.

REM 1. 创建便携式目录
if exist portable rmdir /s /q portable
mkdir portable
echo 1. 创建便携式目录完成

REM 2. 复制应用程序JAR文件
copy /Y target\number-convert-1.0-SNAPSHOT-fat.jar portable\MoneyConverter.jar
if %ERRORLEVEL% NEQ 0 (
    echo 复制JAR文件失败，错误代码: %ERRORLEVEL%
    pause
    exit /b %ERRORLEVEL%
)
echo 2. 复制应用程序JAR文件完成

REM 3. 创建运行脚本
echo @echo off > portable\启动货币转换器.bat
echo echo 正在启动货币转换器应用程序... >> portable\启动货币转换器.bat
echo java -jar MoneyConverter.jar >> portable\启动货币转换器.bat
echo if %%ERRORLEVEL%% NEQ 0 pause >> portable\启动货币转换器.bat
echo 3. 创建运行脚本完成

REM 4. 创建自述文件
echo 货币转换器应用程序 > portable\自述文件.txt
echo ================= >> portable\自述文件.txt
echo. >> portable\自述文件.txt
echo 此应用程序需要安装Java运行时环境(JRE)才能运行。 >> portable\自述文件.txt
echo 您可以从以下网址下载Java: https://www.oracle.com/java/technologies/downloads/ >> portable\自述文件.txt
echo. >> portable\自述文件.txt
echo 使用方法: >> portable\自述文件.txt
echo 1. 双击"启动货币转换器.bat"文件启动应用程序 >> portable\自述文件.txt
echo 2. 请确保您的计算机已安装Java >> portable\自述文件.txt
echo 4. 创建自述文件完成

echo.
echo ===== 便携式应用程序创建完成 =====
echo 便携式应用程序位于 portable 目录中
echo. 