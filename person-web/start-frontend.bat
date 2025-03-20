@echo off
echo ================================
echo 启动个人网站前端应用
echo ================================

set "SCRIPT_DIR=%~dp0"
echo 脚本目录: %SCRIPT_DIR%

echo 0. 检查Node.js版本...
node -v | findstr /r "v[0-9][0-9]\." >nul
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 当前Node.js版本可能不兼容
    echo 建议使用Node.js v16及以上版本
    echo 是否继续? (Y/N)
    set /p choice=
    if /i not "%choice%"=="Y" (
        echo 已取消操作
        exit /b 1
    )
    echo 继续执行，但可能遇到兼容性问题...
) else (
    echo Node.js版本兼容
)

echo 1. 进入前端目录...
cd /d "%SCRIPT_DIR%person-web-ui" || (
    echo [错误] 找不到前端目录: %SCRIPT_DIR%person-web-ui
    echo 请确保在正确的目录运行此脚本
    pause > nul
    exit /b 1
)

echo 2. 检查node_modules是否存在...
if not exist "node_modules\react-scripts" (
    echo node_modules不存在或不完整，需要重新安装...
    
    echo 2.1 尝试删除不完整的node_modules...
    if exist "node_modules" (
        rmdir /s /q node_modules
        if %ERRORLEVEL% NEQ 0 (
            echo [警告] 无法删除旧的node_modules目录，可能需要手动删除
        )
    )
    
    echo 2.2 切换为国内npm镜像源以加速下载...
    call npm config set registry https://registry.npmmirror.com
    
    echo 2.3 安装前端依赖 (这可能需要几分钟)...
    call npm install --legacy-peer-deps
    
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] 安装前端依赖失败!
        echo 尝试使用--force参数安装...
        call npm install --force
        
        if %ERRORLEVEL% NEQ 0 (
            echo [错误] 安装前端依赖仍然失败!
            cd ..
            echo 常见问题:
            echo 1. Node.js未安装或版本过低 (建议版本16+)
            echo 2. npm注册表访问受限
            echo 3. 某些依赖项无法下载
            echo.
            echo 解决方法:
            echo 1. 运行check-env.bat检查环境
            echo 2. 尝试使用如下命令: 
            echo    npm cache clean --force
            echo    npm install --legacy-peer-deps
            echo 3. 确保网络连接正常
            echo.
            echo 按任意键退出...
            pause > nul
            exit /b 1
        ) else (
            echo 使用--force参数安装成功
        )
    ) else (
        echo 前端依赖安装成功
    )
) else (
    echo 前端依赖已安装
)

echo 3. 确保react-scripts正确安装...
if not exist "node_modules\.bin\react-scripts.cmd" (
    echo 未找到react-scripts，尝试单独安装...
    call npm install react-scripts --save
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] 安装react-scripts失败
        echo 尝试最后一种修复方法...
        call npm uninstall react-scripts
        call npm install react-scripts@5.0.1 --save --force
        if %ERRORLEVEL% NEQ 0 (
            echo [错误] 安装react-scripts最终失败
            echo 请手动解决此问题
            cd ..
            exit /b 1
        )
    )
) else (
    echo react-scripts已正确安装
)

echo 4. 启动前端开发服务器...
echo 如果出现"react-scripts"未找到的错误，请按Ctrl+C终止，然后运行:
echo npm uninstall react-scripts
echo npm install react-scripts@5.0.1 --save --force
echo.
echo 正在启动前端服务...
call npm start

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 启动前端服务失败
    echo 尝试修复...
    call npm uninstall react-scripts
    call npm install react-scripts@5.0.1 --save --force
    echo 重新启动...
    call npm start
    
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] 修复后仍然无法启动前端服务
        echo 请检查控制台错误并手动解决问题
    )
)

cd .. 