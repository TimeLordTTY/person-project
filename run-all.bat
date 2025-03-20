@echo off
echo ================================
echo 个人网站系统一键启动脚本
echo ================================

set "ROOT_DIR=%~dp0"
set "WEB_DIR=%ROOT_DIR%person-web"
echo 项目根目录: %ROOT_DIR%
echo Web目录: %WEB_DIR%

echo 1. 检查环境配置...
call "%WEB_DIR%\check-env.bat"

echo 2. 检查是否需要构建后端...
set "JAR_FOUND=false"
set "JAR_PATHS=%WEB_DIR%\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar %ROOT_DIR%person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"

for %%j in (%JAR_PATHS%) do (
    if exist "%%j" (
        echo 后端JAR包已找到: %%j
        set "JAR_FOUND=true"
    )
)

if "%JAR_FOUND%"=="false" (
    echo [提示] 未找到后端JAR包，需要构建后端...
    echo 开始构建后端...
    call "%ROOT_DIR%build-app.bat"
) else (
    echo 后端JAR包已存在，无需重新构建
)

echo 3. 启动前后端服务...
call "%WEB_DIR%\run.bat"

echo ================================
echo 所有服务已启动完成！
echo - 后端API地址: http://localhost:8080/api
echo - 前端页面地址: http://localhost:3000
echo ================================
echo 按任意键退出此脚本 (服务将在后台继续运行)
pause > nul 