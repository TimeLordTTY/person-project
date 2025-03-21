@echo off
echo ===== 创建自包含应用程序包 =====
echo 此脚本将创建一个包含所有必要文件的可分发包

set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo 1. 清理并打包项目...
call ..\mvn-jdk23.cmd clean package
if %ERRORLEVEL% neq 0 goto error

echo 2. 创建分发目录结构...
mkdir dist\lib
mkdir dist\jre

echo 3. 复制应用JAR文件...
copy /Y target\金额转换工具-fat.jar dist\金额转换工具.jar
if %ERRORLEVEL% neq 0 goto error

echo 4. 复制依赖库...
xcopy /Y /E target\lib dist\lib\
if %ERRORLEVEL% neq 0 goto error

echo 5. 提取JRE...
jlink --module-path "%JAVA_HOME%\jmods" --add-modules java.base,java.desktop,java.datatransfer,java.prefs,java.xml,jdk.charsets,jdk.unsupported --output dist\jre --no-header-files --no-man-pages --strip-debug --compress=2
if %ERRORLEVEL% neq 0 goto error

echo 6. 创建启动脚本...
(
echo @echo off
echo set "SCRIPT_DIR=%%~dp0"
echo "%%SCRIPT_DIR%%jre\bin\java" --module-path "%%SCRIPT_DIR%%lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "%%SCRIPT_DIR%%金额转换工具.jar"
) > dist\运行金额转换工具.bat

echo 7. 创建README文件...
(
echo 金额转换工具
echo ===========
echo.
echo 使用方法:
echo 1. 双击 "运行金额转换工具.bat" 文件启动应用程序
echo 2. 或者使用命令行: jre\bin\java --module-path lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar 金额转换工具.jar
echo.
echo 目录结构:
echo - 金额转换工具.jar: 主应用程序
echo - lib/: 依赖库目录，包含JavaFX
echo - jre/: 嵌入式Java运行时
echo - 运行金额转换工具.bat: 启动脚本
) > dist\README.txt

echo 8. 创建ZIP包...
cd dist
7z a -tzip ..\金额转换工具-完整版.zip *
cd ..

echo 完成！完整应用程序包已创建在dist目录下，压缩包为"金额转换工具-完整版.zip"
goto :eof

:error
echo 构建过程中出错，错误代码: %ERRORLEVEL%
exit /b %ERRORLEVEL% 