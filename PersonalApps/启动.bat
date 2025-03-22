@echo off  
echo ================================  
echo 个人应用程序集  
echo ================================  
echo.  
echo 请选择要启动的应用程序:  
echo 1. 货币转换器  
echo 2. 拼写检查器  
echo.  
set /p choice=请输入选项(1-2):  
  
if "%%choice%%"=="1" (  
  start 货币转换器\启动货币转换器.bat  
) else if "%%choice%%"=="2" (  
  start 拼写检查器\启动拼写检查器.bat  
) else (  
  echo 无效选项，请重新运行并输入1或2  
  pause  
) 
