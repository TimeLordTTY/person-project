@echo off
set "JAVA_HOME=D:\Soft\Java\jdk-23"
set "JAVACMD=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_OPTS=-Xmx512m"
E:\Apache\apache-maven-3.9.9\bin\mvn.cmd %*
exit /b %ERRORLEVEL% 
