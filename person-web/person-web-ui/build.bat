@echo off
echo 安装前端依赖...
call npm install

echo 构建前端项目...
call npm run build

echo 前端项目构建完成! 