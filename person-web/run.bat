@echo off
echo ================================
echo 启动个人网站前后端应用
echo ================================

set "SCRIPT_DIR=%~dp0"
echo 脚本目录: %SCRIPT_DIR%

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
    echo [警告] 在相对路径未找到JAR包，尝试其他位置...
    
    if exist "%SCRIPT_DIR%..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" (
        echo 在项目根目录下找到JAR包
        set "JAR_PATH=%SCRIPT_DIR%..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"
        set "JAR_FOUND=true"
    ) else (
        echo [警告] 在当前位置未找到JAR包
        echo 尝试 ..\person-web\person-web-service\target 相对于项目根目录...
        
        if exist "%SCRIPT_DIR%..\person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" (
            echo 在项目根目录相对路径中找到JAR包
            set "JAR_PATH=%SCRIPT_DIR%..\person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"
            set "JAR_FOUND=true"
        ) else (
            echo [错误] 后端JAR包不存在!
            echo 请先执行项目根目录下的build-app.bat构建后端服务
            echo 按任意键退出...
            pause > nul
            exit /b 1
        )
    )
)

if "%JAR_FOUND%"=="false" (
    echo [错误] 无法找到后端JAR包
    echo 按任意键退出...
    pause > nul
    exit /b 1
)

echo 2. 启动后端服务 (SpringBoot)...
echo 使用JAR包: %JAR_PATH%
start cmd /k "echo 正在启动后端服务... && java -jar "%JAR_PATH%""

echo 等待后端服务启动...
echo 这可能需要10-15秒，请耐心等待...
timeout /t 15 /nobreak > nul

echo 3. 检查后端服务是否启动...
ping 127.0.0.1 -n 1 > nul
curl -s http://localhost:8080/api/health 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 无法确认后端服务是否正常启动
    echo 继续启动前端，但可能会出现连接问题
    echo 如果前端无法连接后端，请手动检查后端是否正常运行
) else (
    echo 后端服务已成功启动
)

echo 4. 安装前端依赖...
cd /d "%SCRIPT_DIR%person-web-ui"
if not exist "node_modules" (
    echo 正在安装前端依赖 (这可能需要几分钟)...
    call npm install
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] 安装前端依赖失败!
        cd /d "%SCRIPT_DIR%"
        echo 按任意键退出...
        pause > nul
        exit /b 1
    )
) else (
    echo 前端依赖已安装
)
cd /d "%SCRIPT_DIR%"

echo 5. 启动前端服务 (React)...
start cmd /k "cd /d "%SCRIPT_DIR%person-web-ui" && npm start"

echo ================================
echo 服务正在启动中...
echo - 后端服务: http://localhost:8080/api
echo - 前端服务: http://localhost:3000
echo ================================
echo 提示: 前端服务可能需要等待几秒才能完全启动
echo       如果浏览器没有自动打开, 请手动访问 http://localhost:3000
echo ================================

echo 按任意键退出脚本 (服务会继续在后台运行)...
pause > nul 