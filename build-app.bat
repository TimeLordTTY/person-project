@echo off
echo ================================
echo 构建个人网站后端应用
echo ================================

echo 1. 设置Java环境...
set "JAVA_HOME=D:\Soft\Java\jdk-23"  
set "PATH=D:\Soft\Java\jdk-23\bin;D:\Soft\Java\jdk1.8.0_201\bin;D:\Soft\Java\jdk-23\bin;D:\Soft\Java\jdk1.8.0_201\bin;"D:\Soft\Java\jdk1.8.0_201\jre\bin;";d:\Soft\cursor\resources\app\bin;D:\Soft\Thunder\Program\;C:\Program Files (x86)\Microsoft\Edge\Application;C:\Program Files\Common Files\Oracle\Java\javapath;D:\Soft\Python\Scripts\;D:\Soft\Python\;C:\Python312\Scripts\;C:\Python312\;D:\Soft\VanDyke Software\Clients\;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;C:\windows\system32;C:\windows;C:\windows\System32\Wbem;C:\windows\System32\WindowsPowerShell\v1.0\;C:\windows\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Bandizip\;D:\Soft\TortoiseSVN\bin;C:\Program Files\dotnet\;C:\ProgramData\chocolatey\bin;D:\Soft\Nodejs\;D:\Soft\Nodejs\node_global;C:\Program Files\HP\HP One Agent;C:\Program Files\Pandoc\;E:\Apache\apache-maven-3.6.3\bin;D:\Soft\Git\cmd;C:\Program Files (x86)\OpenOffice 4\program;C:\Program Files\NVIDIA Corporation\NVIDIA app\NvDLISR;d:\Soft\cursor\resources\app\bin;C:\Users\TimeL\AppData\Local\Microsoft\WindowsApps;;D:\Soft\IntelliJ IDEA 2023.3.2\bin;;D:\Soft\Microsoft VS Code\bin;C:\Users\TimeL\AppData\Roaming\npm;C:\Users\TimeL\.dotnet\tools;C:\Users\TimeL\AppData\Local\Programs\Ollama;C:\Users\TimeL\.lmstudio\bin;C:\Users\TimeL\go\bin;D:\Soft\cursor\resources\app\bin"

echo 2. 检查Maven...
set MVN_PATH="E:\Apache\apache-maven-3.9.9\bin\mvn"
if exist %MVN_PATH% (
    echo Maven已找到: %MVN_PATH%
) else (
    echo [警告] 无法找到指定位置的Maven: %MVN_PATH%
    echo 尝试从系统PATH查找Maven...
    where mvn >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo 从系统PATH中找到Maven
        set MVN_PATH=mvn
    ) else (
        echo [错误] 无法找到Maven! 请确保已安装Maven并添加到系统PATH
        echo 或更新此脚本中的MVN_PATH变量指向您的Maven安装路径
        echo 按任意键退出...
        pause > nul
        exit /b 1
    )
)

echo 3. 使用Maven构建后端应用...  
echo 开始编译并打包SpringBoot应用 (跳过测试)...
%MVN_PATH% -f "person-web/person-web-service/pom.xml" clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo [错误] Maven构建失败!
    echo 请检查上面的错误信息
    echo 按任意键退出...
    pause > nul
    exit /b 1
)

echo 4. 验证构建结果...
if exist "person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar" (
    echo ================================
    echo 构建成功! JAR包已生成:
    echo person-web\person-web-service\target\person-web-service-0.0.1-SNAPSHOT.jar
    echo ================================
    echo 现在您可以运行person-web目录下的run.bat启动应用
    echo ================================
) else (
    echo [错误] 无法找到生成的JAR包!
    echo 请检查Maven构建过程中的错误
    echo 按任意键退出...
    pause > nul
    exit /b 1
)

echo 按任意键退出...
pause > nul
