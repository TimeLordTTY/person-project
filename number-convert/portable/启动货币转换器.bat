@echo off 
echo 正在启动货币转换器应用程序... 
java -jar MoneyConverter.jar 
if %ERRORLEVEL% NEQ 0 pause 
