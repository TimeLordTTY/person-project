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
echo 1. 清理分发目录
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
    
    if exist "%DIST_DIR%\文档生成器\*.bat" (
        echo 正在备份文档生成器批处理文件...
        md "%TEMP%\doc_bat_backup" 2>nul
        copy "%DIST_DIR%\文档生成器\*.bat" "%TEMP%\doc_bat_backup\" >nul 2>&1
    )
    
    if exist "%DIST_DIR%\文档生成器\*.txt" (
        echo 正在备份文档生成器说明文档...
        md "%TEMP%\doc_txt_backup" 2>nul
        copy "%DIST_DIR%\文档生成器\*.txt" "%TEMP%\doc_txt_backup\" >nul 2>&1
    )
    
    echo 删除所有生成的文件和目录...
    rd /s /q "%DIST_DIR%" 2>nul
)

echo 创建新的分发目录...
md "%DIST_DIR%" 2>nul
md "%DIST_DIR%\货币转换器" 2>nul
md "%DIST_DIR%\拼写检查器" 2>nul
md "%DIST_DIR%\文档生成器" 2>nul

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

if exist "%TEMP%\doc_bat_backup\*.bat" (
    copy "%TEMP%\doc_bat_backup\*.bat" "%DIST_DIR%\文档生成器\" >nul 2>&1
    rd /s /q "%TEMP%\doc_bat_backup" 2>nul
)

if exist "%TEMP%\doc_txt_backup\*.txt" (
    copy "%TEMP%\doc_txt_backup\*.txt" "%DIST_DIR%\文档生成器\" >nul 2>&1
    rd /s /q "%TEMP%\doc_txt_backup" 2>nul
)

echo ================================
echo 2. 清理所有模块
echo ================================
cd /d "%PROJECT_ROOT%"
call %MVN_CMD% clean -B
if %ERRORLEVEL% NEQ 0 (
    echo 清理失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo ================================
echo 3. 构建安装父项目
echo ================================
call %MVN_CMD% install -DskipTests -B -N
if %ERRORLEVEL% NEQ 0 (
    echo 构建父项目失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo ================================
echo 4. 准备公共JavaFX模块目录
echo ================================
mkdir "%DIST_DIR%\jre" 2>nul
mkdir "%DIST_DIR%\jre\lib" 2>nul
mkdir "%DIST_DIR%\jre\lib\javafx-modules" 2>nul

echo 复制JavaFX库文件...
set "JAVAFX_VERSION=21.0.1"
set "JAVAFX_DIR=E:\Apache\apache-maven-3.9.9\Repository\org\openjfx"

if exist "%JAVAFX_DIR%" (
    copy /Y "%JAVAFX_DIR%\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%-win.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-win.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%-win.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
    copy /Y "%JAVAFX_DIR%\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%-win.jar" "%DIST_DIR%\jre\lib\javafx-modules\"
)

echo ================================
echo 5. 复制公共JRE到分发包
echo ================================
echo 正在复制JRE...
xcopy /E /I /Y "%JAVA_HOME%" "%DIST_DIR%\jre"

echo ================================
echo 6. 构建货币转换器模块
echo ================================
call %MVN_CMD% -B package -DskipTests -pl :number-convert
if %ERRORLEVEL% NEQ 0 (
    echo 构建货币转换器模块失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo 检查EXE文件是否生成...
if not exist "number-convert\target\MoneyConverterTool.exe" (
    echo 警告: 货币转换器EXE文件未生成，尝试备用方案...
    echo 复制JAR文件...
    copy /Y "number-convert\target\number-convert-1.0.0-all.jar" "%DIST_DIR%\货币转换器\货币转换器.jar"
    
    echo 创建启动脚本...
    echo @echo off > "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo echo 正在启动货币转换器... >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo cd /d "%%~dp0" >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo. >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo set JAVA_PATH=..\jre\bin\java.exe >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo set JAR_PATH=货币转换器.jar >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo. >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo if not exist "%%JAVA_PATH%%" ( >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     echo 错误: 找不到Java运行环境，请确保jre目录存在 >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     pause >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo ) >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo. >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo if not exist "%%JAR_PATH%%" ( >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     echo 错误: 找不到应用程序JAR文件 >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     pause >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo ) >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo. >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo "%%JAVA_PATH%%" --module-path=..\jre\lib\javafx-modules --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar "%%JAR_PATH%%" >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
) else (
    echo 复制货币转换器EXE文件...
    copy /Y "number-convert\target\MoneyConverterTool.exe" "%DIST_DIR%\货币转换器\货币转换器.exe"
    
    echo 创建EXE启动脚本...
    echo @echo off > "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo echo 正在启动货币转换器... >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo cd /d "%%~dp0" >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo. >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo if not exist "货币转换器.exe" ( >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     echo 错误: 找不到货币转换器可执行文件 >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     pause >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo ) >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo. >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    echo start "" "货币转换器.exe" >> "%DIST_DIR%\货币转换器\启动货币转换器.bat"
    
    echo 修改EXE配置使用公共JRE...
    copy /Y "number-convert\target\*.properties" "%DIST_DIR%\货币转换器\"
)

echo ================================
echo 7. 构建拼写检查器模块
echo ================================
call %MVN_CMD% -B package -DskipTests -pl :spelling-test
if %ERRORLEVEL% NEQ 0 (
    echo 构建拼写检查器模块失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo 检查EXE文件是否生成...
if not exist "spelling-test\target\SpellCheckTool.exe" (
    echo 警告: 拼写检查器EXE文件未生成，尝试备用方案...
    echo 复制JAR文件...
    copy /Y "spelling-test\target\spelling-test-1.0.0-all.jar" "%DIST_DIR%\拼写检查器\拼写检查器.jar"
    
    echo 创建启动脚本...
    echo @echo off > "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo echo 正在启动拼写检查器... >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo cd /d "%%~dp0" >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo. >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo set JAVA_PATH=..\jre\bin\java.exe >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo set JAR_PATH=拼写检查器.jar >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo. >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo if not exist "%%JAVA_PATH%%" ( >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     echo 错误: 找不到Java运行环境，请确保jre目录存在 >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     pause >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo ) >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo. >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo if not exist "%%JAR_PATH%%" ( >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     echo 错误: 找不到应用程序JAR文件 >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     pause >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo ) >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo. >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo "%%JAVA_PATH%%" --module-path=..\jre\lib\javafx-modules --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar "%%JAR_PATH%%" >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
) else (
    echo 复制拼写检查器EXE文件...
    copy /Y "spelling-test\target\SpellCheckTool.exe" "%DIST_DIR%\拼写检查器\拼写检查器.exe"
    
    echo 创建EXE启动脚本...
    echo @echo off > "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo echo 正在启动拼写检查器... >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo cd /d "%%~dp0" >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo. >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo if not exist "拼写检查器.exe" ( >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     echo 错误: 找不到拼写检查器可执行文件 >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     pause >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo ) >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo. >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    echo start "" "拼写检查器.exe" >> "%DIST_DIR%\拼写检查器\启动拼写检查器.bat"
    
    echo 复制配置文件...
    copy /Y "spelling-test\target\*.xml" "%DIST_DIR%\拼写检查器\"
    copy /Y "spelling-test\target\*.properties" "%DIST_DIR%\拼写检查器\"
)

echo ================================
echo 8. 构建文档生成器模块
echo ================================
call %MVN_CMD% -B package -DskipTests -pl :doc-generator
if %ERRORLEVEL% NEQ 0 (
    echo 构建文档生成器模块失败，错误代码: %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo ================================
echo 8.1 生成Word和Excel模板文件
echo ================================
echo 清理文档生成器模板目录...
echo 删除旧的模板文件...
if exist "doc-generator\src\main\resources\templates\project_report.docx" (
    del /f /q "doc-generator\src\main\resources\templates\project_report.docx"
)
if exist "doc-generator\src\main\resources\templates\project_status.xlsx" (
    del /f /q "doc-generator\src\main\resources\templates\project_status.xlsx"
)

echo 执行Word模板生成器...
cd /d "%PROJECT_ROOT%"
call %MVN_CMD% exec:java -Dexec.mainClass="com.timelordtty.docgen.utils.DocxTemplateGenerator" -Dexec.args="--enable-preview" -pl :doc-generator
if %ERRORLEVEL% NEQ 0 (
    echo 警告: Word模板生成失败，将创建占位符文件...
    echo "This is a placeholder for Word template file. Please generate a proper template." > "doc-generator\src\main\resources\templates\project_report.docx"
)

echo 执行Excel模板生成器...
cd /d "%PROJECT_ROOT%"
call %MVN_CMD% exec:java -Dexec.mainClass="com.timelordtty.docgen.utils.ExcelTemplateGenerator" -Dexec.args="--enable-preview" -pl :doc-generator
if %ERRORLEVEL% NEQ 0 (
    echo 警告: Excel模板生成失败，将创建占位符文件...
    echo "This is a placeholder for Excel template file. Please generate a proper template." > "doc-generator\src\main\resources\templates\project_status.xlsx"
)

echo 检查模板文件是否生成...
if not exist "doc-generator\src\main\resources\templates\project_report.docx" (
    echo 创建Word模板占位符文件...
    echo "This is a placeholder for Word template file. Please generate a proper template." > "doc-generator\src\main\resources\templates\project_report.docx"
)
if not exist "doc-generator\src\main\resources\templates\project_status.xlsx" (
    echo 创建Excel模板占位符文件...
    echo "This is a placeholder for Excel template file. Please generate a proper template." > "doc-generator\src\main\resources\templates\project_status.xlsx"
)

echo 检查EXE文件是否生成...
if not exist "doc-generator\target\DocGeneratorTool.exe" (
    echo 警告: 文档生成器EXE文件未生成，尝试备用方案...
    echo 复制JAR文件...
    copy /Y "doc-generator\target\doc-generator-1.0.0-all.jar" "%DIST_DIR%\文档生成器\文档生成器.jar"
    
    echo 创建启动脚本...
    echo @echo off > "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo echo 正在启动文档生成器... >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo cd /d "%%~dp0" >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo. >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo set JAVA_PATH=..\jre\bin\java.exe >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo set JAR_PATH=文档生成器.jar >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo. >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo if not exist "%%JAVA_PATH%%" ( >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     echo 错误: 找不到Java运行环境，请确保jre目录存在 >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     pause >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo ) >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo. >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo if not exist "%%JAR_PATH%%" ( >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     echo 错误: 找不到应用程序JAR文件 >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     pause >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo ) >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo. >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo "%%JAVA_PATH%%" --module-path=..\jre\lib\javafx-modules --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar "%%JAR_PATH%%" >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
) else (
    echo 复制文档生成器EXE文件...
    copy /Y "doc-generator\target\DocGeneratorTool.exe" "%DIST_DIR%\文档生成器\文档生成器.exe"
    
    echo 创建EXE启动脚本...
    echo @echo off > "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo echo 正在启动文档生成器... >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo cd /d "%%~dp0" >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo. >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo if not exist "文档生成器.exe" ( >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     echo 错误: 找不到文档生成器可执行文件 >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     pause >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo     exit /b 1 >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo ) >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo. >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
    echo start "" "文档生成器.exe" >> "%DIST_DIR%\文档生成器\启动文档生成器.bat"
)

echo 复制自定义模板和数据文件...
echo 创建模板目录...
mkdir "%DIST_DIR%\文档生成器\templates" 2>nul

echo 清理目标目录中的旧文件...
del /f /q "%DIST_DIR%\文档生成器\templates\*.*" 2>nul

echo 复制JSON数据文件...
copy /Y "doc-generator\src\main\resources\templates\sample_data.json" "%DIST_DIR%\文档生成器\templates\"

echo 复制Word和Excel模板文件...
copy /Y "doc-generator\src\main\resources\templates\project_report.docx" "%DIST_DIR%\文档生成器\templates\"
copy /Y "doc-generator\src\main\resources\templates\project_status.xlsx" "%DIST_DIR%\文档生成器\templates\"

echo 复制说明文件...
copy /Y "doc-generator\src\main\resources\templates\模板说明.txt" "%DIST_DIR%\文档生成器\templates\"

echo 创建测试输出目录...
mkdir "%DIST_DIR%\文档生成器\output" 2>nul

echo ================================
echo 9. 更新根目录启动脚本
echo ================================
echo @echo off > "%DIST_DIR%\启动.bat"
echo echo ================================ >> "%DIST_DIR%\启动.bat"
echo echo 个人应用程序集 >> "%DIST_DIR%\启动.bat"
echo echo ================================ >> "%DIST_DIR%\启动.bat"
echo echo. >> "%DIST_DIR%\启动.bat"
echo echo 请选择要启动的应用程序: >> "%DIST_DIR%\启动.bat"
echo echo 1. 货币转换器 >> "%DIST_DIR%\启动.bat"
echo echo 2. 拼写检查器 >> "%DIST_DIR%\启动.bat"
echo echo 3. 文档生成器 >> "%DIST_DIR%\启动.bat"
echo echo. >> "%DIST_DIR%\启动.bat"
echo set /p choice=请输入选项(1-3): >> "%DIST_DIR%\启动.bat"
echo. >> "%DIST_DIR%\启动.bat"

echo if "%%choice%%"=="1" ( >> "%DIST_DIR%\启动.bat"
echo   if exist "货币转换器\货币转换器.exe" ( >> "%DIST_DIR%\启动.bat"
echo     cd /d "%%~dp0\货币转换器" >> "%DIST_DIR%\启动.bat"
echo     start "" "货币转换器.exe" >> "%DIST_DIR%\启动.bat"
echo   ) else ( >> "%DIST_DIR%\启动.bat"
echo     call "货币转换器\启动货币转换器.bat" >> "%DIST_DIR%\启动.bat"
echo   ) >> "%DIST_DIR%\启动.bat"
echo ) else if "%%choice%%"=="2" ( >> "%DIST_DIR%\启动.bat"
echo   if exist "拼写检查器\拼写检查器.exe" ( >> "%DIST_DIR%\启动.bat"
echo     cd /d "%%~dp0\拼写检查器" >> "%DIST_DIR%\启动.bat"
echo     start "" "拼写检查器.exe" >> "%DIST_DIR%\启动.bat"
echo   ) else ( >> "%DIST_DIR%\启动.bat"
echo     call "拼写检查器\启动拼写检查器.bat" >> "%DIST_DIR%\启动.bat"
echo   ) >> "%DIST_DIR%\启动.bat"
echo ) else if "%%choice%%"=="3" ( >> "%DIST_DIR%\启动.bat"
echo   if exist "文档生成器\文档生成器.exe" ( >> "%DIST_DIR%\启动.bat"
echo     cd /d "%%~dp0\文档生成器" >> "%DIST_DIR%\启动.bat"
echo     start "" "文档生成器.exe" >> "%DIST_DIR%\启动.bat"
echo   ) else ( >> "%DIST_DIR%\启动.bat"
echo     call "文档生成器\启动文档生成器.bat" >> "%DIST_DIR%\启动.bat"
echo   ) >> "%DIST_DIR%\启动.bat"
echo ) else ( >> "%DIST_DIR%\启动.bat"
echo   echo 无效选项，请重新运行并输入1-3的数字 >> "%DIST_DIR%\启动.bat"
echo   pause >> "%DIST_DIR%\启动.bat"
echo ) >> "%DIST_DIR%\启动.bat"

echo 更新使用指南...
echo 个人应用程序集使用指南 > "%DIST_DIR%\使用指南.txt"
echo =================== >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 此程序包含三个应用程序，无需安装Java即可运行。 >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 启动方法: >> "%DIST_DIR%\使用指南.txt"
echo 1. 双击"启动.bat"批处理文件，然后选择要启动的应用程序。 >> "%DIST_DIR%\使用指南.txt"
echo 2. 或者直接进入对应的应用程序目录，双击其中的EXE文件或启动批处理文件。 >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 包含的应用程序: >> "%DIST_DIR%\使用指南.txt"
echo - 货币转换器: 提供货币和数字转换功能 >> "%DIST_DIR%\使用指南.txt"
echo - 拼写检查器: 提供文本拼写检查功能 >> "%DIST_DIR%\使用指南.txt"
echo - 文档生成器: 提供基于Word和Excel模板生成文档功能 >> "%DIST_DIR%\使用指南.txt"
echo. >> "%DIST_DIR%\使用指南.txt"
echo 版本: 1.0.0 >> "%DIST_DIR%\使用指南.txt"
echo 发布日期: %date% >> "%DIST_DIR%\使用指南.txt"

echo ================================
echo 构建完成!
echo ================================
echo 分发包已创建在: %CD%\%DIST_DIR%\
echo 您可以将整个%DIST_DIR%目录复制给其他用户，他们可以直接运行应用程序。
echo ================================

echo 按任意键退出...
pause > nul 