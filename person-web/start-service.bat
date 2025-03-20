@echo off
chcp 65001 >nul
echo ================================
echo 个人网站系统一键启动脚本
echo ================================

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"
echo 当前目录: %CD%

echo 0. 检查Java环境...
java -version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未检测到Java环境!
    echo 请安装Java或设置正确的环境变量后再试
    echo 按任意键退出...
    pause > nul
    exit /b 1
) else (
    echo Java环境正常
)

echo 1. 检查后端JAR包是否存在...
set "JAR_PATH=%SCRIPT_DIR%person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"
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

echo 3. 启动后端服务 (SpringBoot)...
echo 使用JAR包: %JAR_PATH%
start cmd /k "echo 正在启动后端服务... && java -jar "%JAR_PATH%""

echo 等待后端服务启动...
echo 这可能需要10-15秒，请耐心等待...
timeout /t 15 /nobreak > nul

echo 4. 检查后端服务是否启动...
ping 127.0.0.1 -n 1 > nul
curl -s http://127.0.0.1:8080/api/users >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 无法确认后端服务是否正常启动
    echo 继续启动前端，但可能会出现连接问题
    echo 如果前端无法连接后端，请手动检查后端是否正常运行
) else (
    echo 后端服务已成功启动
)

echo 5. 检查前端依赖...
cd /d "%SCRIPT_DIR%person-web-ui"

echo 是否需要修复前端项目配置？ (Y/N)
choice /C YN /M "选择Y安装配置和修复依赖，选择N使用现有配置"
if %ERRORLEVEL% EQU 1 (
    echo 正在安装自定义配置工具...
    call npm install react-app-rewired customize-cra --save
    
    echo 确保.env文件存在...
    echo WDS_SOCKET_HOST=127.0.0.1> .env
    echo DANGEROUSLY_DISABLE_HOST_CHECK=true>> .env
    echo HOST=127.0.0.1>> .env
    
    echo 正在修复ajv依赖问题...
    call npm install ajv@^8.0.0 ajv-keywords@^5.0.0 --save
) else (
    echo 使用现有前端配置
)

echo 6. 启动前端服务...
echo 设置环境变量防止webpack配置问题...
set "HOST=127.0.0.1"
set "WDS_SOCKET_HOST=127.0.0.1"
set "DANGEROUSLY_DISABLE_HOST_CHECK=true"

echo 检查hosts文件设置...
findstr /C:"127.0.0.1 localhost" %WINDIR%\System32\drivers\etc\hosts >nul
if %ERRORLEVEL% NEQ 0 (
    echo [警告] hosts文件可能没有正确设置localhost
    echo 使用IP地址127.0.0.1代替localhost
)

start cmd /k "cd /d "%SCRIPT_DIR%person-web-ui" && set HOST=127.0.0.1 && set WDS_SOCKET_HOST=127.0.0.1 && set DANGEROUSLY_DISABLE_HOST_CHECK=true && npm start"

echo 等待前端服务启动...
timeout /t 10 /nobreak > nul

echo 7. 打开浏览器...
start http://127.0.0.1:3000

echo ================================
echo 服务已成功启动!
echo 后端服务: http://127.0.0.1:8080/api
echo 前端服务: http://127.0.0.1:3000
echo ================================
echo 提示: 前端页面需要几秒加载，浏览器已自动打开
echo ================================

echo 按任意键退出脚本 (服务会继续在后台运行)...
pause > nul 