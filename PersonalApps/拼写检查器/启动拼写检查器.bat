@echo off  
echo 正在启动拼写检查器...  
cd "%%~dp0\.."  
"%%~dp0\..\jre\bin\java" -jar "%%~dp0\拼写检查器.jar" 
