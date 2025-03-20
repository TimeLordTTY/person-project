@echo off
echo ================================
echo 临时修复脚本
echo ================================

echo 显示当前环境信息...
echo 当前目录: %CD%
echo 脚本目录: %~dp0

echo 检查脚本是否都在正确位置...
set "WEB_DIR=%~dp0person-web"
set "ROOT_DIR=%~dp0"

echo person-web目录: %WEB_DIR%
echo 根目录: %ROOT_DIR%

echo 下面是person-web目录中的文件:
dir /b "%WEB_DIR%"

echo 确保脚本都在可访问的位置...

if exist "%WEB_DIR%\check-env.bat" (
    echo check-env.bat 在person-web目录中存在
) else (
    echo [错误] check-env.bat 在person-web目录中不存在
)

if exist "%WEB_DIR%\run.bat" (
    echo run.bat 在person-web目录中存在
) else (
    echo [错误] run.bat 在person-web目录中不存在
)

if exist "%WEB_DIR%\start-frontend.bat" (
    echo start-frontend.bat 在person-web目录中存在
) else (
    echo [错误] start-frontend.bat 在person-web目录中不存在
)

echo 检查JAR包位置...
set "JAR_PATHS=%WEB_DIR%\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar %ROOT_DIR%person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar %ROOT_DIR%person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"

set "JAR_FOUND=false"
for %%j in (%JAR_PATHS%) do (
    if exist "%%j" (
        echo JAR包找到: %%j
        set "JAR_FOUND=true"
        set "JAR_PATH=%%j"
    )
)

if "%JAR_FOUND%"=="false" (
    echo [错误] 未找到JAR包，请确保项目已正确构建
)

echo 尝试执行前端启动脚本...
echo 1. 首先尝试从根目录调用...
cd "%ROOT_DIR%"
call person-web\start-frontend.bat

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 从根目录调用失败，尝试切换到person-web目录...
    cd "%WEB_DIR%"
    call start-frontend.bat
)

echo ================================
echo 处理完成
echo 按任意键退出...
pause > nul 