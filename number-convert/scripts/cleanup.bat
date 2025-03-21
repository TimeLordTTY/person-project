@echo off
echo ===== 清理不需要的脚本文件 =====

cd /d "%~dp0\.."
echo 当前工作目录: %CD%

echo 确认删除旧的脚本文件...
if exist scripts\create-exe.bat del /q scripts\create-exe.bat
if exist scripts\create-dist.bat del /q scripts\create-dist.bat
if exist launch4j-config.xml del /q launch4j-config.xml

echo 重命名新脚本...
if exist scripts\single-create-exe.bat rename scripts\single-create-exe.bat create-exe.bat

echo.
echo 清理完成！现在只保留了一个简化的脚本: scripts\create-exe.bat
echo. 