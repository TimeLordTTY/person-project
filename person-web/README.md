# Steam个人资料网站

这是一个基于Steam API的个人资料展示网站，采用前后端分离架构。

## 项目结构

- `person-web-service`: SpringBoot后端项目
- `person-web-ui`: React前端项目

## 环境要求

- JDK 11 或更高版本
- Node.js 16 或更高版本
- npm 8 或更高版本
- 网络连接（用于下载依赖）

## 快速开始

### 方法一：使用一键启动脚本

1. 首次运行前，请构建前后端项目：

```
cd person-web-service
build.bat
cd ../person-web-ui
build.bat
```

2. 然后使用以下命令启动前后端服务：

```
run.bat
```

3. 访问 http://localhost:3000 查看个人资料网站

### 方法二：分别启动

#### 后端服务

1. 进入后端目录：

```
cd person-web-service
```

2. 构建项目：

```
gradlew build
```

3. 运行应用：

```
java -jar build/libs/person-web-service.jar
```

4. 后端API将在 http://localhost:8080/api 上运行

#### 前端应用

1. 进入前端目录：

```
cd person-web-ui
```

2. 安装依赖：

```
npm install
```

3. 启动开发服务器：

```
npm start
```

4. 前端应用将在 http://localhost:3000 上运行

## 功能说明

- 展示Steam用户资料信息
- 支持刷新资料
- 响应式设计，适配各种设备

## API文档

### 获取用户资料

- **URL**: `/api/profile`
- **方法**: GET
- **响应**: 用户资料对象

### 刷新用户资料

- **URL**: `/api/profile/refresh`
- **方法**: POST
- **响应**: 更新后的用户资料对象 