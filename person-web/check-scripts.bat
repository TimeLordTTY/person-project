@echo off
echo ================================
echo 启动脚本自检工具
echo ================================

set "ERROR_COUNT=0"
set "SCRIPT_DIR=%~dp0"
echo 当前脚本目录: %SCRIPT_DIR%

echo 1. 检查脚本文件是否存在...
set "SCRIPTS=check-env.bat run.bat start-frontend.bat"
for %%s in (%SCRIPTS%) do (
    if not exist "%SCRIPT_DIR%%%s" (
        echo [错误] 未找到脚本: %SCRIPT_DIR%%%s
        set /a ERROR_COUNT+=1
    ) else (
        echo 脚本存在: %%s
    )
)

echo 2. 检查后端JAR文件是否存在...
set "JAR_FOUND=false"
set "JAR_PATHS=%SCRIPT_DIR%person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar %SCRIPT_DIR%..\person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"

for %%j in (%JAR_PATHS%) do (
    if exist "%%j" (
        echo 后端JAR包已找到: %%j
        set "JAR_FOUND=true"
        set "JAR_PATH=%%j"
    )
)

if "%JAR_FOUND%"=="false" (
    echo [错误] 未找到后端JAR包，尝试在其他位置查找...
    
    rem 尝试在项目根目录下查找
    if exist "%SCRIPT_DIR%..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" (
        echo 在项目根目录下找到JAR包: %SCRIPT_DIR%..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar
        set "JAR_FOUND=true"
        set "JAR_PATH=%SCRIPT_DIR%..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar"
    ) else (
        echo [错误] 未找到后端JAR包，请确保已构建后端项目
        set /a ERROR_COUNT+=1
    )
)

echo 3. 检查环境变量...
echo 3.1 检查Java...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] Java未安装或环境变量未正确设置
    set /a ERROR_COUNT+=1
) else (
    echo Java环境正常
)

echo 3.2 检查Node.js...
node -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] Node.js未安装或环境变量未正确设置
    set /a ERROR_COUNT+=1
) else (
    echo Node.js环境正常
)

echo 3.3 检查npm...
npm -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] npm未安装或环境变量未正确设置
    set /a ERROR_COUNT+=1
) else (
    echo npm环境正常
)

echo 4. 检查前端目录结构...
if not exist "%SCRIPT_DIR%person-web-ui" (
    echo [错误] 未找到前端目录: %SCRIPT_DIR%person-web-ui
    set /a ERROR_COUNT+=1
) else (
    echo 前端目录存在: %SCRIPT_DIR%person-web-ui
    
    if not exist "%SCRIPT_DIR%person-web-ui\package.json" (
        echo [错误] 前端缺少package.json文件
        set /a ERROR_COUNT+=1
    ) else (
        echo 前端package.json文件存在
    )
)

echo 5. 检查后端目录结构...
if not exist "%SCRIPT_DIR%person-web-service" (
    echo [错误] 未找到后端目录: %SCRIPT_DIR%person-web-service
    set /a ERROR_COUNT+=1
) else (
    echo 后端目录存在: %SCRIPT_DIR%person-web-service
    
    if not exist "%SCRIPT_DIR%person-web-service\pom.xml" (
        echo [警告] 后端目录缺少pom.xml文件
    ) else (
        echo 后端pom.xml文件存在
    )
)

echo 6. 项目路径情况...
echo 当前目录: %CD%
echo 脚本目录: %SCRIPT_DIR%
echo 项目根目录: %SCRIPT_DIR%..

echo ================================
echo 自检结果摘要
echo ================================

if %ERROR_COUNT% GTR 0 (
    echo [警告] 自检发现 %ERROR_COUNT% 个问题需要解决
    echo 是否自动创建缺失的脚本文件? (Y/N)
    set /p create_scripts=
    if /i "%create_scripts%"=="Y" (
        echo 开始创建缺失的脚本文件...
        
        if not exist "%SCRIPT_DIR%check-env.bat" (
            echo 创建check-env.bat...
            echo @echo off > "%SCRIPT_DIR%check-env.bat"
            echo echo ================================ >> "%SCRIPT_DIR%check-env.bat"
            echo echo 检查开发环境配置 >> "%SCRIPT_DIR%check-env.bat"
            echo echo ================================ >> "%SCRIPT_DIR%check-env.bat"
            echo. >> "%SCRIPT_DIR%check-env.bat"
            echo echo 检查Node.js... >> "%SCRIPT_DIR%check-env.bat"
            echo node --version >> "%SCRIPT_DIR%check-env.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%check-env.bat"
            echo     echo [错误] Node.js未安装或PATH环境变量未正确设置! >> "%SCRIPT_DIR%check-env.bat"
            echo     echo 请安装Node.js^^^(建议版本16+^^^)，网址: https://nodejs.org/ >> "%SCRIPT_DIR%check-env.bat"
            echo     goto :end >> "%SCRIPT_DIR%check-env.bat"
            echo ^) else ( >> "%SCRIPT_DIR%check-env.bat"
            echo     echo Node.js已安装 >> "%SCRIPT_DIR%check-env.bat"
            echo ^) >> "%SCRIPT_DIR%check-env.bat"
            echo. >> "%SCRIPT_DIR%check-env.bat"
            echo echo 检查npm... >> "%SCRIPT_DIR%check-env.bat"
            echo npm --version >> "%SCRIPT_DIR%check-env.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%check-env.bat"
            echo     echo [错误] npm未安装或PATH环境变量未正确设置! >> "%SCRIPT_DIR%check-env.bat"
            echo     echo npm通常随Node.js一起安装，请重新安装Node.js >> "%SCRIPT_DIR%check-env.bat"
            echo     goto :end >> "%SCRIPT_DIR%check-env.bat"
            echo ^) else ( >> "%SCRIPT_DIR%check-env.bat"
            echo     echo npm已安装 >> "%SCRIPT_DIR%check-env.bat"
            echo ^) >> "%SCRIPT_DIR%check-env.bat"
            echo. >> "%SCRIPT_DIR%check-env.bat"
            echo echo 检查Java... >> "%SCRIPT_DIR%check-env.bat"
            echo java -version >> "%SCRIPT_DIR%check-env.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%check-env.bat"
            echo     echo [警告] Java未安装或PATH环境变量未正确设置! >> "%SCRIPT_DIR%check-env.bat"
            echo     echo 需要安装JDK以运行后端服务 >> "%SCRIPT_DIR%check-env.bat"
            echo ^) else ( >> "%SCRIPT_DIR%check-env.bat"
            echo     echo Java已安装 >> "%SCRIPT_DIR%check-env.bat"
            echo ^) >> "%SCRIPT_DIR%check-env.bat"
            echo. >> "%SCRIPT_DIR%check-env.bat"
            echo echo ================================ >> "%SCRIPT_DIR%check-env.bat"
            echo echo 环境检查完成 >> "%SCRIPT_DIR%check-env.bat"
            echo echo ================================ >> "%SCRIPT_DIR%check-env.bat"
            echo. >> "%SCRIPT_DIR%check-env.bat"
            echo :end >> "%SCRIPT_DIR%check-env.bat"
            echo pause >> "%SCRIPT_DIR%check-env.bat"
            echo 已创建check-env.bat
        )
        
        if not exist "%SCRIPT_DIR%run.bat" (
            echo 创建run.bat...
            echo @echo off > "%SCRIPT_DIR%run.bat"
            echo echo ================================ >> "%SCRIPT_DIR%run.bat"
            echo echo 启动个人网站前后端应用 >> "%SCRIPT_DIR%run.bat"
            echo echo ================================ >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 0. 检查Java环境... >> "%SCRIPT_DIR%run.bat"
            echo java -version 2^>nul >> "%SCRIPT_DIR%run.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%run.bat"
            echo     echo [错误] 未检测到Java环境! >> "%SCRIPT_DIR%run.bat"
            echo     echo 请安装Java或设置正确的环境变量后再试 >> "%SCRIPT_DIR%run.bat"
            echo     echo 按任意键退出... >> "%SCRIPT_DIR%run.bat"
            echo     pause ^> nul >> "%SCRIPT_DIR%run.bat"
            echo     exit /b 1 >> "%SCRIPT_DIR%run.bat"
            echo ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo     echo Java环境正常 >> "%SCRIPT_DIR%run.bat"
            echo ^) >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 1. 检查后端JAR包是否存在... >> "%SCRIPT_DIR%run.bat"
            echo set "JAR_PATH=person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" >> "%SCRIPT_DIR%run.bat"
            echo set "JAR_FOUND=false" >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo if exist "%%JAR_PATH%%" ( >> "%SCRIPT_DIR%run.bat"
            echo     echo 后端JAR包已找到: %%JAR_PATH%% >> "%SCRIPT_DIR%run.bat"
            echo     set "JAR_FOUND=true" >> "%SCRIPT_DIR%run.bat"
            echo ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo     echo [警告] 在相对路径未找到JAR包，尝试绝对路径... >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo     if exist "%%~dp0person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" ( >> "%SCRIPT_DIR%run.bat"
            echo         echo 在绝对路径中找到JAR包 >> "%SCRIPT_DIR%run.bat"
            echo         set "JAR_PATH=%%~dp0person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" >> "%SCRIPT_DIR%run.bat"
            echo         set "JAR_FOUND=true" >> "%SCRIPT_DIR%run.bat"
            echo     ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo         echo [警告] 在当前位置未找到JAR包 >> "%SCRIPT_DIR%run.bat"
            echo         echo 尝试 ..\person-web\person-web-service\target 相对于项目根目录... >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo         if exist "..\person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" ( >> "%SCRIPT_DIR%run.bat"
            echo             echo 在项目根目录相对路径中找到JAR包 >> "%SCRIPT_DIR%run.bat"
            echo             set "JAR_PATH=..\person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" >> "%SCRIPT_DIR%run.bat"
            echo             set "JAR_FOUND=true" >> "%SCRIPT_DIR%run.bat"
            echo         ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo             if exist "..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" ( >> "%SCRIPT_DIR%run.bat"
            echo                 echo 在项目根目录下找到JAR包 >> "%SCRIPT_DIR%run.bat"
            echo                 set "JAR_PATH=..\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" >> "%SCRIPT_DIR%run.bat"
            echo                 set "JAR_FOUND=true" >> "%SCRIPT_DIR%run.bat"
            echo             ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo                 echo [错误] 后端JAR包不存在! >> "%SCRIPT_DIR%run.bat"
            echo                 echo 请先执行项目根目录下的build-app.bat构建后端服务 >> "%SCRIPT_DIR%run.bat"
            echo                 echo 按任意键退出... >> "%SCRIPT_DIR%run.bat"
            echo                 pause ^> nul >> "%SCRIPT_DIR%run.bat"
            echo                 exit /b 1 >> "%SCRIPT_DIR%run.bat"
            echo             ^) >> "%SCRIPT_DIR%run.bat"
            echo         ^) >> "%SCRIPT_DIR%run.bat"
            echo     ^) >> "%SCRIPT_DIR%run.bat"
            echo ^) >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo if "%%JAR_FOUND%%"=="false" ( >> "%SCRIPT_DIR%run.bat"
            echo     echo [错误] 无法找到后端JAR包 >> "%SCRIPT_DIR%run.bat"
            echo     echo 按任意键退出... >> "%SCRIPT_DIR%run.bat"
            echo     pause ^> nul >> "%SCRIPT_DIR%run.bat"
            echo     exit /b 1 >> "%SCRIPT_DIR%run.bat"
            echo ^) >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 2. 启动后端服务 ^(SpringBoot^)... >> "%SCRIPT_DIR%run.bat"
            echo echo 使用JAR包: %%JAR_PATH%% >> "%SCRIPT_DIR%run.bat"
            echo start cmd /k "echo 正在启动后端服务... ^&^& java -jar "%%JAR_PATH%%"" >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 等待后端服务启动... >> "%SCRIPT_DIR%run.bat"
            echo echo 这可能需要10-15秒，请耐心等待... >> "%SCRIPT_DIR%run.bat"
            echo timeout /t 15 /nobreak ^> nul >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 3. 检查后端服务是否启动... >> "%SCRIPT_DIR%run.bat"
            echo ping 127.0.0.1 -n 1 ^> nul >> "%SCRIPT_DIR%run.bat"
            echo curl -s http://localhost:8080/api/users 2^>nul >> "%SCRIPT_DIR%run.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%run.bat"
            echo     echo [警告] 无法确认后端服务是否正常启动 >> "%SCRIPT_DIR%run.bat"
            echo     echo 继续启动前端，但可能会出现连接问题 >> "%SCRIPT_DIR%run.bat"
            echo     echo 如果前端无法连接后端，请手动检查后端是否正常运行 >> "%SCRIPT_DIR%run.bat"
            echo ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo     echo 后端服务已成功启动 >> "%SCRIPT_DIR%run.bat"
            echo ^) >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 4. 安装前端依赖... >> "%SCRIPT_DIR%run.bat"
            echo cd person-web-ui >> "%SCRIPT_DIR%run.bat"
            echo if not exist "node_modules" ( >> "%SCRIPT_DIR%run.bat"
            echo     echo 正在安装前端依赖 ^(这可能需要几分钟^)... >> "%SCRIPT_DIR%run.bat"
            echo     call npm install --legacy-peer-deps >> "%SCRIPT_DIR%run.bat"
            echo     if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%run.bat"
            echo         echo [错误] 安装前端依赖失败! >> "%SCRIPT_DIR%run.bat"
            echo         echo 尝试使用--force参数安装... >> "%SCRIPT_DIR%run.bat"
            echo         call npm install --force >> "%SCRIPT_DIR%run.bat"
            echo         if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%run.bat"
            echo             echo [错误] 安装前端依赖仍然失败! >> "%SCRIPT_DIR%run.bat"
            echo             cd .. >> "%SCRIPT_DIR%run.bat"
            echo             echo 按任意键退出... >> "%SCRIPT_DIR%run.bat"
            echo             pause ^> nul >> "%SCRIPT_DIR%run.bat"
            echo             exit /b 1 >> "%SCRIPT_DIR%run.bat"
            echo         ^) >> "%SCRIPT_DIR%run.bat"
            echo     ^) >> "%SCRIPT_DIR%run.bat"
            echo ^) else ( >> "%SCRIPT_DIR%run.bat"
            echo     echo 前端依赖已安装 >> "%SCRIPT_DIR%run.bat"
            echo ^) >> "%SCRIPT_DIR%run.bat"
            echo cd .. >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo 5. 启动前端服务 ^(React^)... >> "%SCRIPT_DIR%run.bat"
            echo start cmd /k "cd person-web-ui ^&^& npm start" >> "%SCRIPT_DIR%run.bat"
            echo. >> "%SCRIPT_DIR%run.bat"
            echo echo ================================ >> "%SCRIPT_DIR%run.bat"
            echo echo 服务正在启动中... >> "%SCRIPT_DIR%run.bat"
            echo echo - 后端服务: http://localhost:8080/api >> "%SCRIPT_DIR%run.bat"
            echo echo - 前端服务: http://localhost:3000 >> "%SCRIPT_DIR%run.bat"
            echo echo ================================ >> "%SCRIPT_DIR%run.bat"
            echo echo 提示: 前端服务可能需要等待几秒才能完全启动 >> "%SCRIPT_DIR%run.bat"
            echo echo       如果浏览器没有自动打开, 请手动访问 http://localhost:3000 >> "%SCRIPT_DIR%run.bat"
            echo echo ================================ >> "%SCRIPT_DIR%run.bat"
            echo echo 按任意键退出脚本 ^(服务会继续在后台运行^)... >> "%SCRIPT_DIR%run.bat"
            echo pause ^> nul >> "%SCRIPT_DIR%run.bat"
            echo 已创建run.bat
        )
        
        if not exist "%SCRIPT_DIR%start-frontend.bat" (
            echo 创建start-frontend.bat...
            echo @echo off > "%SCRIPT_DIR%start-frontend.bat"
            echo echo ================================ >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 启动个人网站前端应用 >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo ================================ >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 0. 检查Node.js版本... >> "%SCRIPT_DIR%start-frontend.bat"
            echo node -v ^| findstr /r "v[0-9][0-9]\." ^>nul >> "%SCRIPT_DIR%start-frontend.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo [警告] 当前Node.js版本可能不兼容 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 建议使用Node.js v16及以上版本 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 是否继续? ^(Y/N^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo     set /p choice= >> "%SCRIPT_DIR%start-frontend.bat"
            echo     if /i not "%%choice%%"=="Y" ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo 已取消操作 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         exit /b 1 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 继续执行，但可能遇到兼容性问题... >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) else ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo Node.js版本兼容 >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 1. 进入前端目录... >> "%SCRIPT_DIR%start-frontend.bat"
            echo cd "%%~dp0person-web-ui" ^|^| ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo [错误] 找不到前端目录: %%~dp0person-web-ui >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 请确保在正确的目录运行此脚本 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     pause ^> nul >> "%SCRIPT_DIR%start-frontend.bat"
            echo     exit /b 1 >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 2. 检查node_modules是否存在... >> "%SCRIPT_DIR%start-frontend.bat"
            echo if not exist "node_modules\react-scripts" ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo node_modules不存在或不完整，需要重新安装... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 2.1 尝试删除不完整的node_modules... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     if exist "node_modules" ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo         rmdir /s /q node_modules >> "%SCRIPT_DIR%start-frontend.bat"
            echo         if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo [警告] 无法删除旧的node_modules目录，可能需要手动删除 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo     ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo     >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 2.2 切换为国内npm镜像源以加速下载... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     call npm config set registry https://registry.npmmirror.com >> "%SCRIPT_DIR%start-frontend.bat"
            echo     >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 2.3 安装前端依赖 ^(这可能需要几分钟^)... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     call npm install --legacy-peer-deps >> "%SCRIPT_DIR%start-frontend.bat"
            echo     >> "%SCRIPT_DIR%start-frontend.bat"
            echo     if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo [错误] 安装前端依赖失败! >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo 尝试使用--force参数安装... >> "%SCRIPT_DIR%start-frontend.bat"
            echo         call npm install --force >> "%SCRIPT_DIR%start-frontend.bat"
            echo         >> "%SCRIPT_DIR%start-frontend.bat"
            echo         if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo [错误] 安装前端依赖仍然失败! >> "%SCRIPT_DIR%start-frontend.bat"
            echo             cd .. >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 常见问题: >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 1. Node.js未安装或版本过低 ^(建议版本16+^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 2. npm注册表访问受限 >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 3. 某些依赖项无法下载 >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 解决方法: >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 1. 运行check-env.bat检查环境 >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 2. 尝试使用如下命令:  >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo    npm cache clean --force >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo    npm install --legacy-peer-deps >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 3. 确保网络连接正常 >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 按任意键退出... >> "%SCRIPT_DIR%start-frontend.bat"
            echo             pause ^> nul >> "%SCRIPT_DIR%start-frontend.bat"
            echo             exit /b 1 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         ^) else ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 使用--force参数安装成功 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo     ^) else ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo 前端依赖安装成功 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) else ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 前端依赖已安装 >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 3. 确保react-scripts正确安装... >> "%SCRIPT_DIR%start-frontend.bat"
            echo if not exist "node_modules\.bin\react-scripts.cmd" ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 未找到react-scripts，尝试单独安装... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     call npm install react-scripts --save >> "%SCRIPT_DIR%start-frontend.bat"
            echo     if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo [错误] 安装react-scripts失败 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo 尝试最后一种修复方法... >> "%SCRIPT_DIR%start-frontend.bat"
            echo         call npm uninstall react-scripts >> "%SCRIPT_DIR%start-frontend.bat"
            echo         call npm install react-scripts@5.0.1 --save --force >> "%SCRIPT_DIR%start-frontend.bat"
            echo         if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo [错误] 安装react-scripts最终失败 >> "%SCRIPT_DIR%start-frontend.bat"
            echo             echo 请手动解决此问题 >> "%SCRIPT_DIR%start-frontend.bat"
            echo             cd .. >> "%SCRIPT_DIR%start-frontend.bat"
            echo             exit /b 1 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo     ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) else ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo react-scripts已正确安装 >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 4. 启动前端开发服务器... >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 如果出现"react-scripts"未找到的错误，请按Ctrl+C终止，然后运行: >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo npm uninstall react-scripts >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo npm install react-scripts@5.0.1 --save --force >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo echo 正在启动前端服务... >> "%SCRIPT_DIR%start-frontend.bat"
            echo call npm start >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo [错误] 启动前端服务失败 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 尝试修复... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     call npm uninstall react-scripts >> "%SCRIPT_DIR%start-frontend.bat"
            echo     call npm install react-scripts@5.0.1 --save --force >> "%SCRIPT_DIR%start-frontend.bat"
            echo     echo 重新启动... >> "%SCRIPT_DIR%start-frontend.bat"
            echo     call npm start >> "%SCRIPT_DIR%start-frontend.bat"
            echo     >> "%SCRIPT_DIR%start-frontend.bat"
            echo     if %%ERRORLEVEL%% NEQ 0 ^( >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo [错误] 修复后仍然无法启动前端服务 >> "%SCRIPT_DIR%start-frontend.bat"
            echo         echo 请检查控制台错误并手动解决问题 >> "%SCRIPT_DIR%start-frontend.bat"
            echo     ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo ^) >> "%SCRIPT_DIR%start-frontend.bat"
            echo. >> "%SCRIPT_DIR%start-frontend.bat"
            echo cd .. >> "%SCRIPT_DIR%start-frontend.bat"
            echo 已创建start-frontend.bat
        )
        
        echo 所有缺失的脚本文件已创建完成，请重新运行check-scripts.bat进行检查
    )
) else (
    echo [成功] 所有检查通过，系统应该可以正常启动
    echo 推荐使用以下步骤启动应用:
    echo 1. 执行 ..\build-app.bat 构建后端 (如需重新构建)
    echo 2. 执行 run.bat 启动前后端应用
)

echo ================================
echo 按任意键退出...
pause > nul 