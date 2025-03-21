@echo off
set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"

java --module-path "%JAVA_HOME%\lib;target\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -jar target\金额转换工具-fat.jar
pause 