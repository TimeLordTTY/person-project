@echo off 
echo 正在启动拼写检查器... 
cd /d "%~dp0" 
 
if not exist "拼写检查器.exe" ( 
    echo 错误: 找不到拼写检查器可执行文件 
    pause 
    exit /b 1 
) 
 
start "" "拼写检查器.exe" 
