@echo off 
echo 正在启动货币转换器... 
cd /d "%~dp0" 
 
if not exist "货币转换器.exe" ( 
    echo 错误: 找不到货币转换器可执行文件 
    pause 
    exit /b 1 
) 
 
start "" "货币转换器.exe" 
