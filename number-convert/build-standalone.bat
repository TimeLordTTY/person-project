@echo off
echo ===== 构建独立可执行应用程序 =====
echo 此脚本将创建一个包含JRE和JavaFX的独立可执行程序

set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo 1. 清理并打包项目...
call ..\mvn-jdk23.cmd clean package
if %ERRORLEVEL% neq 0 goto error

echo 2. 创建自定义JRE镜像...
jlink --module-path "%JAVA_HOME%\jmods" --add-modules java.base,java.desktop,java.logging,java.xml,java.sql,java.management,java.naming,jdk.unsupported --output target\custom-jre --compress=2 --no-header-files --no-man-pages --strip-debug
if %ERRORLEVEL% neq 0 goto error

echo 3. 复制JavaFX模块...
mkdir target\custom-jre\lib\javafx
xcopy /Y /S "target\lib\javafx-*.jar" "target\custom-jre\lib\javafx\"
if %ERRORLEVEL% neq 0 goto error

echo 4. 创建启动脚本...
echo @echo off > target\金额转换工具.bat
echo set "SCRIPT_DIR=%%~dp0" >> target\金额转换工具.bat
echo "%%SCRIPT_DIR%%custom-jre\bin\java" --module-path "%%SCRIPT_DIR%%lib;%%SCRIPT_DIR%%custom-jre\lib\javafx" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar "%%SCRIPT_DIR%%金额转换工具-fat.jar" >> target\金额转换工具.bat

echo 5. 复制其他所需文件到目标目录...
mkdir target\dist
xcopy /Y "target\金额转换工具-fat.jar" "target\dist\"
xcopy /Y "target\金额转换工具.bat" "target\dist\"
xcopy /Y /S "target\custom-jre" "target\dist\custom-jre\"
xcopy /Y /S "target\lib" "target\dist\lib\"

echo 6. 使用Launch4j创建可执行文件...
cd target\dist
echo ^<?xml version="1.0" encoding="UTF-8"?^> > config.xml
echo ^<launch4jConfig^> >> config.xml
echo     ^<dontWrapJar^>false^</dontWrapJar^> >> config.xml
echo     ^<headerType^>gui^</headerType^> >> config.xml
echo     ^<jar^>金额转换工具-fat.jar^</jar^> >> config.xml
echo     ^<outfile^>金额转换工具.exe^</outfile^> >> config.xml
echo     ^<errTitle^>金额转换工具^</errTitle^> >> config.xml
echo     ^<cmdLine^>^</cmdLine^> >> config.xml
echo     ^<chdir^>^</chdir^> >> config.xml
echo     ^<priority^>normal^</priority^> >> config.xml
echo     ^<downloadUrl^>^</downloadUrl^> >> config.xml
echo     ^<supportUrl^>^</supportUrl^> >> config.xml
echo     ^<stayAlive^>false^</stayAlive^> >> config.xml
echo     ^<restartOnCrash^>false^</restartOnCrash^> >> config.xml
echo     ^<manifest^>^</manifest^> >> config.xml
echo     ^<icon^>^</icon^> >> config.xml
echo     ^<jre^> >> config.xml
echo         ^<path^>custom-jre^</path^> >> config.xml
echo         ^<bundledJre64Bit^>true^</bundledJre64Bit^> >> config.xml
echo         ^<bundledJreAsFallback^>false^</bundledJreAsFallback^> >> config.xml
echo         ^<minVersion^>^</minVersion^> >> config.xml
echo         ^<maxVersion^>^</maxVersion^> >> config.xml
echo         ^<jdkPreference^>jreOnly^</jdkPreference^> >> config.xml
echo         ^<runtimeBits^>64^</runtimeBits^> >> config.xml
echo         ^<opt^>--module-path "lib;custom-jre\lib\javafx" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base^</opt^> >> config.xml
echo     ^</jre^> >> config.xml
echo ^</launch4jConfig^> >> config.xml

echo 7. 创建安装包...
echo 完成！独立应用程序已创建在 target\dist 目录中

goto :eof

:error
echo 构建过程中出错，错误代码: %ERRORLEVEL%
exit /b %ERRORLEVEL% 