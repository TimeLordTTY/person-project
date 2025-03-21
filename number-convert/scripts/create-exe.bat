@echo off
setlocal EnableDelayedExpansion

echo ===== 创建完整的独立可执行程序 =====
echo 此脚本将创建一个包含JRE和JavaFX的独立可执行文件

set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "MAVEN_HOME=E:\Apache\apache-maven-3.9.9"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0\.."
echo 当前工作目录: %CD%

:: 从pom.xml读取主类名
for /f "tokens=2 delims=>< " %%a in ('findstr /c:"<main.class>" pom.xml') do (
  set "MAIN_CLASS=%%a"
)
echo 主类名: %MAIN_CLASS%

:: 清理旧的构建文件
echo 清理旧的构建文件...
if exist dist (
  echo 尝试删除旧的dist目录...
  rd /s /q dist 2>nul
  if exist dist (
    echo 无法删除dist目录，可能有文件正在使用中
    echo 将尝试使用新目录名...
    set "DIST_DIR=dist_%random%"
  ) else (
    set "DIST_DIR=dist"
  )
) else (
  set "DIST_DIR=dist"
)

echo 1. 构建项目...
call "%MAVEN_HOME%\bin\mvn" clean package -DskipTests
if %ERRORLEVEL% neq 0 goto error

echo 2. 创建最终分发目录 %DIST_DIR%...
mkdir %DIST_DIR%
mkdir %DIST_DIR%\lib
mkdir %DIST_DIR%\jre

echo 3. 复制JavaFX库文件...
set "JAVAFX_DIR=%MAVEN_HOME%\Repository\org\openjfx"
echo   复制JavaFX JAR文件...
xcopy /Y "%JAVAFX_DIR%\javafx-graphics\17.0.2\javafx-graphics-17.0.2.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-controls\17.0.2\javafx-controls-17.0.2.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-base\17.0.2\javafx-base-17.0.2.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-base\17.0.2\javafx-base-17.0.2-win.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar" %DIST_DIR%\lib\
xcopy /Y "%JAVAFX_DIR%\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar" %DIST_DIR%\lib\

echo 4. 复制应用JAR文件...
copy /Y target\number-convert-1.0-SNAPSHOT-fat.jar %DIST_DIR%\货币转换器.jar
if %ERRORLEVEL% neq 0 goto error

echo 5. 复制可执行文件...
copy /Y target\MoneyConverter.exe %DIST_DIR%\货币转换器.exe
if %ERRORLEVEL% neq 0 goto error

echo 6. 创建JRE目录链接...
echo :: 此部分使用现有JDK创建符号链接，无需重新创建JRE
mklink /D %DIST_DIR%\jre "%JAVA_HOME%"
if %ERRORLEVEL% neq 0 (
  echo 创建符号链接失败，尝试复制JRE...
  xcopy /E /I /Y "%JAVA_HOME%" %DIST_DIR%\jre
)

echo 7. 创建启动脚本...
echo @echo off > %DIST_DIR%\启动货币转换器.bat
echo echo 正在启动货币转换器... >> %DIST_DIR%\启动货币转换器.bat
echo cd "%%~dp0" >> %DIST_DIR%\启动货币转换器.bat
echo "%%~dp0jre\bin\java" -cp "%%~dp0货币转换器.jar;%%~dp0lib\*" %MAIN_CLASS% >> %DIST_DIR%\启动货币转换器.bat

echo 8. 创建说明文件...
echo 货币转换器应用程序 > %DIST_DIR%\使用说明.txt
echo =================== >> %DIST_DIR%\使用说明.txt
echo. >> %DIST_DIR%\使用说明.txt
echo 此应用程序包含自己的Java运行环境，不需要您的计算机安装Java。 >> %DIST_DIR%\使用说明.txt
echo. >> %DIST_DIR%\使用说明.txt
echo 使用方法: >> %DIST_DIR%\使用说明.txt
echo 方法1: 双击"货币转换器.exe"可执行文件启动应用程序(推荐)。 >> %DIST_DIR%\使用说明.txt
echo 方法2: 双击"启动货币转换器.bat"批处理文件启动应用程序。 >> %DIST_DIR%\使用说明.txt
echo. >> %DIST_DIR%\使用说明.txt
echo 版本: 1.0.0 >> %DIST_DIR%\使用说明.txt
echo 发布日期: %date% >> %DIST_DIR%\使用说明.txt

echo.
echo 完成！独立可执行程序已创建在 %DIST_DIR% 目录中。
echo 您可以将整个%DIST_DIR%目录复制到任何电脑上运行，无需安装Java环境。
echo.
goto :eof

:error
echo 处理过程中出错，错误代码: %ERRORLEVEL%
exit /b %ERRORLEVEL% 