@echo off
echo 设置环境变量...
set JAVA_HOME=D:\Soft\Java\jdk-23
set PATH=%JAVA_HOME%\bin;%PATH%
set MAVEN_OPTS=-Xmx1024m -Dfile.encoding=UTF-8

echo 当前使用的Java版本:
java -version

echo 验证JAVA_HOME设置:
echo JAVA_HOME=%JAVA_HOME%

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo 错误: Java可执行文件不在指定的JAVA_HOME路径中
    exit /b 1
)

echo 使用Maven 3.9.9执行构建...

echo 构建父项目...
call E:\Apache\apache-maven-3.9.9\bin\mvn -B clean install -DskipTests

echo 构建所有子模块...
call E:\Apache\apache-maven-3.9.9\bin\mvn -B clean package -DskipTests

echo 构建完成! 