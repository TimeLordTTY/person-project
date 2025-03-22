@echo off 
echo 正在启动货币转换器... 
cd "%~dp0\.." 
"%~dp0\..\jre\bin\java" -cp "%~dp0\货币转换器.jar;%~dp0\..\lib\javafx\*" com.timelordtty.convert.MoneyConverterApp 
