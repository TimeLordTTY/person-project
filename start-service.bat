@echo off
echo ================================
echo 个人网站系统一键启动脚本
echo ================================

cd /d "%~dp0"
echo 当前目录: %CD%

echo 1. 检查后端JAR包是否存在...
set "JAR_PATH=person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"
set "JAR_FOUND=false"

if exist "%JAR_PATH%" (
    echo 后端JAR包已找到: %JAR_PATH%
    set "JAR_FOUND=true"
) else (
    echo [警告] 后端JAR包不存在，需要先构建...
    
    echo 2. 执行构建脚本...
    call build-app.bat
    
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] 构建失败，请检查上述错误信息
        pause > nul
        exit /b 1
    )
    
    if exist "%JAR_PATH%" (
        echo 后端JAR包已成功构建
        set "JAR_FOUND=true"
    ) else (
        echo [错误] 构建后仍未找到JAR包，请检查项目配置
        pause > nul
        exit /b 1
    )
)

echo 3. 启动前后端服务...
cd person-web
call run.bat

echo ================================
echo 所有服务已启动完成!
echo 如果浏览器未自动打开，请访问:
echo http://localhost:3000
echo ================================ 