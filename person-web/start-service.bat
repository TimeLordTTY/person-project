@echo off
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
curl -s http://localhost:8080/api/users >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 无法确认后端服务是否正常启动
    echo 继续启动前端，但可能会出现连接问题
    echo 如果前端无法连接后端，请手动检查后端是否正常运行
) else (
    echo 后端服务已成功启动
)

echo 5. 检查前端依赖...
cd /d "%SCRIPT_DIR%person-web-ui"
if not exist "node_modules" (
    echo 前端依赖不存在，正在安装...
    call npm install --legacy-peer-deps
    if %ERRORLEVEL% NEQ 0 (
        call npm install --force
    )
) else (
    echo 前端依赖已安装
)

echo 6. 启动前端服务...
start cmd /k "cd /d "%SCRIPT_DIR%person-web-ui" && npm start"

echo 等待前端服务启动...
timeout /t 5 /nobreak > nul

echo 7. 打开浏览器...
start http://localhost:3000

echo ================================
echo 服务已成功启动!
echo - 后端服务: http://localhost:8080/api
echo - 前端服务: http://localhost:3000
echo ================================
echo 提示: 前端服务可能需要几秒才能完全启动
echo       浏览器已自动打开，请等待页面加载完成
echo ================================

echo 按任意键退出脚本 (服务会继续在后台运行)...
pause > nul 