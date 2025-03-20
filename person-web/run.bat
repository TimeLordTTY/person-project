@echo off
echo ================================
echo 启动个人网站前后端应用
echo ================================

echo 1. 启动后端服务 (SpringBoot)...
start cmd /k "cd person-web-service && java -jar build/libs/person-web-service.jar"

echo 等待后端服务启动...
timeout /t 10 /nobreak > nul

echo 2. 启动前端服务 (React)...
start cmd /k "cd person-web-ui && npm start"

echo ================================
echo 服务已启动
echo - 后端服务: http://localhost:8080/api
echo - 前端服务: http://localhost:3000
echo ================================

echo 按任意键退出...
pause > nul 