@echo off
echo ================================
echo 个人项目全局构建脚本
echo ================================

echo 设置环境变量...
set JAVA_HOME=D:\Soft\Java\jdk-23
set PATH=%JAVA_HOME%\bin;%PATH%
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8
set MVN_CMD=E:\Apache\apache-maven-3.9.9\bin\mvn
set PROJECT_ROOT=%~dp0

echo 当前使用的Java版本:
java -version

echo 验证JAVA_HOME设置:
echo JAVA_HOME=%JAVA_HOME%

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo 错误: Java可执行文件不在指定的JAVA_HOME路径中
    exit /b 1
)

echo ================================
echo 0. 清理分发目录
echo ================================
set "DIST_DIR=PersonalApps"
if exist %DIST_DIR% (
    echo 正在清理 %DIST_DIR% 目录...
    
    rem 保留批处理文件和说明文档
    if exist "%DIST_DIR%\*.bat" (
        echo 正在备份批处理文件...
        md "%TEMP%\bat_backup" 2>nul
        copy "%DIST_DIR%\*.bat" "%TEMP%\bat_backup\" >nul 2>&1
    )
    
    if exist "%DIST_DIR%\*.txt" (
        echo 正在备份说明文档...
        md "%TEMP%\txt_backup" 2>nul
        copy "%DIST_DIR%\*.txt" "%TEMP%\txt_backup\" >nul 2>&1
    )
    
    if exist "%DIST_DIR%\货币转换器\*.bat" (
        echo 正在备份货币转换器批处理文件...
        md "%TEMP%\money_bat_backup" 2>nul
        copy "%DIST_DIR%\货币转换器\*.bat" "%TEMP%\money_bat_backup\" >nul 2>&1
    )
    
    if exist "%DIST_DIR%\货币转换器\*.txt" (
        echo 正在备份货币转换器说明文档...
        md "%TEMP%\money_txt_backup" 2>nul
        copy "%DIST_DIR%\货币转换器\*.txt" "%TEMP%\money_txt_backup\" >nul 2>&1
    )
    
    if exist "%DIST_DIR%\拼写检查器\*.bat" (
        echo 正在备份拼写检查器批处理文件...
        md "%TEMP%\spell_bat_backup" 2>nul
        copy "%DIST_DIR%\拼写检查器\*.bat" "%TEMP%\spell_bat_backup\" >nul 2>&1
    )
    
    if exist "%DIST_DIR%\拼写检查器\*.txt" (
        echo 正在备份拼写检查器说明文档...
        md "%TEMP%\spell_txt_backup" 2>nul
        copy "%DIST_DIR%\拼写检查器\*.txt" "%TEMP%\spell_txt_backup\" >nul 2>&1
    )
    
    echo 删除所有生成的文件和目录...
    rd /s /q "%DIST_DIR%" 2>nul
    
    echo 创建新的分发目录...
    md "%DIST_DIR%" 2>nul
    md "%DIST_DIR%\货币转换器" 2>nul
    md "%DIST_DIR%\拼写检查器" 2>nul
    
    echo 恢复批处理文件和说明文档...
    if exist "%TEMP%\bat_backup\*.bat" (
        copy "%TEMP%\bat_backup\*.bat" "%DIST_DIR%\" >nul 2>&1
        rd /s /q "%TEMP%\bat_backup" 2>nul
    )
    
    if exist "%TEMP%\txt_backup\*.txt" (
        copy "%TEMP%\txt_backup\*.txt" "%DIST_DIR%\" >nul 2>&1
        rd /s /q "%TEMP%\txt_backup" 2>nul
    )
    
    if exist "%TEMP%\money_bat_backup\*.bat" (
        copy "%TEMP%\money_bat_backup\*.bat" "%DIST_DIR%\货币转换器\" >nul 2>&1
        rd /s /q "%TEMP%\money_bat_backup" 2>nul
    )
    
    if exist "%TEMP%\money_txt_backup\*.txt" (
        copy "%TEMP%\money_txt_backup\*.txt" "%DIST_DIR%\货币转换器\" >nul 2>&1
        rd /s /q "%TEMP%\money_txt_backup" 2>nul
    )
    
    if exist "%TEMP%\spell_bat_backup\*.bat" (
        copy "%TEMP%\spell_bat_backup\*.bat" "%DIST_DIR%\拼写检查器\" >nul 2>&1
        rd /s /q "%TEMP%\spell_bat_backup" 2>nul
    )
    
    if exist "%TEMP%\spell_txt_backup\*.txt" (
        copy "%TEMP%\spell_txt_backup\*.txt" "%DIST_DIR%\拼写检查器\" >nul 2>&1
        rd /s /q "%TEMP%\spell_txt_backup" 2>nul
    )
) else (
    echo 创建新的分发目录...
    md "%DIST_DIR%" 2>nul
    md "%DIST_DIR%\货币转换器" 2>nul
    md "%DIST_DIR%\拼写检查器" 2>nul
)

echo ================================
echo 1. 清理所有模块
echo ================================
call %MVN_CMD% clean -B
if %ERRORLEVEL% NEQ 0 (
    echo 清理失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo ================================
echo 2. 构建安装父项目
echo ================================
call %MVN_CMD% install -DskipTests -B -N
if %ERRORLEVEL% NEQ 0 (
    echo 构建父项目失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo ================================
echo 3. 构建所有子模块
echo ================================
call %MVN_CMD% package -DskipTests -B
if %ERRORLEVEL% NEQ 0 (
    echo 构建子模块失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo ================================
echo 4. 在根目录创建分发包
echo ================================
cd /d "%PROJECT_ROOT%"
echo 当前工作目录: %CD%

echo 4.2 创建新的分发目录结构...
mkdir %DIST_DIR%\lib
mkdir %DIST_DIR%\lib\javafx

echo 4.3 复制JRE环境(确保应用无需本地Java环境)...
echo 注意: 这可能需要几分钟时间，请耐心等待...
xcopy /E /I /Y "%JAVA_HOME%" %DIST_DIR%\jre

echo 4.4 复制JavaFX库文件...
set "JAVAFX_VERSION=21.0.1"
set "JAVAFX_DIR=E:\Apache\apache-maven-3.9.9\Repository\org\openjfx"
xcopy /Y "%JAVAFX_DIR%\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%-win.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-win.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%-win.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%.jar" %DIST_DIR%\lib\javafx\
xcopy /Y "%JAVAFX_DIR%\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%-win.jar" %DIST_DIR%\lib\javafx\

echo ================================
echo 5. 准备货币转换器应用
echo ================================
cd /d "%PROJECT_ROOT%\number-convert"
echo 当前工作目录: %CD%

echo 5.1 确认JAR文件存在...
if not exist target\number-convert-1.0.0.jar (
  echo 错误: number-convert-1.0.0.jar 文件不存在，构建可能失败
  echo 尝试再次构建number-convert模块...
  cd /d "%PROJECT_ROOT%"
  call %MVN_CMD% -B package -DskipTests -pl :number-convert
  cd /d "%PROJECT_ROOT%\number-convert"
)

echo 5.2 复制应用JAR文件...
copy /Y target\number-convert-1.0.0.jar "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\货币转换器.jar"
if %ERRORLEVEL% NEQ 0 (
  echo 错误: 无法复制货币转换器JAR文件!
  exit /b %ERRORLEVEL%
)

echo 5.3 创建启动脚本...
echo @echo off > "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\启动货币转换器.bat"
echo echo 正在启动货币转换器... >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\启动货币转换器.bat"
echo cd "%%~dp0\.." >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\启动货币转换器.bat"
echo "%%~dp0\..\jre\bin\java" -cp "%%~dp0\货币转换器.jar;%%~dp0\..\lib\javafx\*" com.timelordtty.convert.MoneyConverterApp >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\启动货币转换器.bat"

echo 5.4 创建说明文件...
echo 货币转换器应用程序 > "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo =================== >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo. >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo 此应用程序包含自己的Java运行环境，不需要您的计算机安装Java。 >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo. >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo 使用方法: >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo 双击"启动货币转换器.bat"批处理文件启动应用程序。 >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo. >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo 版本: 1.0.0 >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"
echo 发布日期: %date% >> "%PROJECT_ROOT%\%DIST_DIR%\货币转换器\使用说明.txt"

echo ================================
echo 6. 准备拼写检查器应用
echo ================================
cd /d "%PROJECT_ROOT%\spelling-test"
echo 当前工作目录: %CD%

echo 6.1 确认JAR文件存在...
if not exist target\spelling-test-1.0.0-all.jar (
  echo 错误: spelling-test-1.0.0-all.jar 文件不存在，尝试再次构建spelling-test模块...
  cd /d "%PROJECT_ROOT%"
  call %MVN_CMD% -B package -DskipTests -pl :spelling-test
  cd /d "%PROJECT_ROOT%\spelling-test"
  
  if not exist target\spelling-test-1.0.0-all.jar (
    echo 严重错误: 无法生成拼写检查器JAR文件，检查构建日志了解详情
    echo 检查target目录现有内容...
    dir target\*.jar
    echo 继续尝试寻找其他可能的JAR文件...
    
    rem 寻找可能的替代JAR文件
    set "FOUND_JAR="
    for %%F in (target\spelling-test*.jar) do (
      echo 找到JAR文件: %%F
      set "FOUND_JAR=%%F"
    )
    
    if not "%FOUND_JAR%"=="" (
      echo 使用替代JAR文件: %FOUND_JAR%
      copy /Y "%FOUND_JAR%" "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\拼写检查器.jar"
      if %ERRORLEVEL% NEQ 0 (
        echo 错误: 无法复制替代拼写检查器JAR文件!
        exit /b %ERRORLEVEL%
      )
    ) else (
      echo 严重错误: 未找到任何可用的JAR文件，跳过拼写检查器构建
      exit /b 1
    )
  )
)

echo 6.2 复制应用JAR文件...
if exist target\spelling-test-1.0.0-all.jar (
  copy /Y target\spelling-test-1.0.0-all.jar "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\拼写检查器.jar"
  if %ERRORLEVEL% NEQ 0 (
    echo 错误: 无法复制拼写检查器JAR文件!
    exit /b %ERRORLEVEL%
  )
)

echo 6.3 创建启动脚本...
echo @echo off > "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
echo echo 正在启动拼写检查器... >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
echo cd "%%~dp0\.." >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
echo "%%~dp0\..\jre\bin\java" -jar "%%~dp0\拼写检查器.jar" >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\启动拼写检查器.bat"

echo 6.4 创建说明文件...
echo 拼写检查器应用程序 > "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo =================== >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo. >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo 此应用程序包含自己的Java运行环境，不需要您的计算机安装Java。 >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo. >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo 使用方法: >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo 双击"启动拼写检查器.bat"批处理文件启动应用程序。 >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo. >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo 版本: 1.0.0 >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"
echo 发布日期: %date% >> "%PROJECT_ROOT%\%DIST_DIR%\拼写检查器\使用说明.txt"

echo ================================
echo 7. 创建根目录启动页面
echo ================================
cd /d "%PROJECT_ROOT%"
echo 当前工作目录: %CD%

echo @echo off > "%DIST_DIR%\启动.bat"
echo echo ================================ >> "%DIST_DIR%\启动.bat"
echo echo 个人应用程序集 >> "%DIST_DIR%\启动.bat"
echo echo ================================ >> "%DIST_DIR%\启动.bat"
echo echo. >> "%DIST_DIR%\启动.bat"
echo echo 请选择要启动的应用程序: >> "%DIST_DIR%\启动.bat"
echo echo 1. 货币转换器 >> "%DIST_DIR%\启动.bat"
echo echo 2. 拼写检查器 >> "%DIST_DIR%\启动.bat"
echo echo. >> "%DIST_DIR%\启动.bat"
echo set /p choice=请输入选项(1-2): >> "%DIST_DIR%\启动.bat"
echo. >> "%DIST_DIR%\启动.bat"
echo if "%%choice%%"=="1" ( >> "%DIST_DIR%\启动.bat"
echo   start 货币转换器\启动货币转换器.bat >> "%DIST_DIR%\启动.bat"
echo ) else if "%%choice%%"=="2" ( >> "%DIST_DIR%\启动.bat"
echo   start 拼写检查器\启动拼写检查器.bat >> "%DIST_DIR%\启动.bat"
echo ) else ( >> "%DIST_DIR%\启动.bat"
echo   echo 无效选项，请重新运行并输入1或2 >> "%DIST_DIR%\启动.bat"
echo   pause >> "%DIST_DIR%\启动.bat"
echo ) >> "%DIST_DIR%\启动.bat"

echo 创建使用指南...
echo 个人应用程序集使用指南 > "%DIST_DIR%\使用指南.txt"
echo =================== >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 此程序包含两个应用程序，共享一个Java运行环境，不需要您的计算机安装Java。 >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 启动方法: >> "%DIST_DIR%\使用指南.txt"
echo 1. 双击"启动.bat"批处理文件，然后选择要启动的应用程序。 >> "%DIST_DIR%\使用指南.txt"
echo 2. 或者直接进入对应的应用程序目录，双击其中的启动批处理文件。 >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 包含的应用程序: >> "%DIST_DIR%\使用指南.txt"
echo - 货币转换器: 提供货币和数字转换功能 >> "%DIST_DIR%\使用指南.txt"
echo - 拼写检查器: 提供文本拼写检查功能 >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 版本: 1.0.0 >> "%DIST_DIR%\使用指南.txt"
echo 发布日期: %date% >> "%DIST_DIR%\使用指南.txt"

echo ================================
echo 构建完成!
echo ================================
echo 分发包已创建在: %CD%\%DIST_DIR%\
echo 您可以将整个%DIST_DIR%目录复制给没有Java环境的用户，他们可以直接运行应用程序。
echo ================================

echo 按任意键退出...
pause > nul 