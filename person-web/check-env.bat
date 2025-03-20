@echo off
echo ================================
echo 检查开发环境配置
echo ================================

set "SCRIPT_DIR=%~dp0"
echo 脚本目录: %SCRIPT_DIR%

echo 检查Node.js...
node --version
if %ERRORLEVEL% NEQ 0 (
    echo [错误] Node.js未安装或PATH环境变量未正确设置!
    echo 请安装Node.js^^^(建议版本16+^^^)，网址: https://nodejs.org/
    goto :end
) else (
    echo Node.js已安装
)

echo 检查npm...
npm --version
if %ERRORLEVEL% NEQ 0 (
    echo [错误] npm未安装或PATH环境变量未正确设置!
    echo npm通常随Node.js一起安装，请重新安装Node.js
    goto :end
) else (
    echo npm已安装
)

echo 检查Java...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo [警告] Java未安装或PATH环境变量未正确设置!
    echo 需要安装JDK以运行后端服务
) else (
    echo Java已安装
)

echo ================================
echo 环境检查完成
echo ================================

:end
pause 