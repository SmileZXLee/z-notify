# Z-Notify - 管理后台 (admin)

<p align="center">
    <img alt="logo" src="https://admin.z-notify.zxlee.cn/logo.png" width="100" height="100" style="margin-bottom: 20px;">
</p>

`admin` 目录是 `Z-Notify` 平台的 Web 后台管理系统模块。基于 Vue 2 与 Element UI 构建，提供给系统管理员使用。

## 🛠️ 主要功能
- **项目管理**：支持多项目（应用）独立配置隔离。
- **版本控制**：配置发布新版本、热更新下载地址以及强更选项。
- **通知公告**：发布过期自动失效的系统通知。
- **云配置/通用文本**：可视化的动态 KV 配置面板。
- **反馈处理**：查看用户在移动端提交的反馈内容，支持管理员回复、标记解决状态。
- **流量统计分析**：以图表形式直观展示各应用的每日访问次数、PV/UV、地域分布及访问来源。

---

## 🚀 运行与部署指南

### 1. 准备工作
请确保你已安装了 [Node.js](https://nodejs.org/)（推荐 v14.x 或 v16.x）和包管理工具 `npm` / `yarn`。

### 2. 进入目录并安装依赖
在最外层项目根目录下，先进入 `admin` 文件夹：

```bash
cd admin
```

安装所需依赖：
```bash
# 使用 yarn 安装
yarn install

# 或者使用 npm 安装
npm install
```

### 3. 修改配置
根据需要修改环境变量配置文件：
- **开发环境配置**：**[.env.development](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin/.env.development)**（默认对接 `http://localhost:8901` 后端服务）
- **生产环境配置**：**[.env](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin/.env)** / **[.env.preview](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin/.env.preview)**

### 4. 启动开发模式
运行以下命令启动本地开发热重载服务器：
```bash
yarn run serve
# 或者 npm run serve
```
启动成功后，可在浏览器访问：`http://localhost:8900`（或控制台输出的本地端口）。

### 5. 编译打包
用于生产环境部署的构建命令：
```bash
yarn run build
# 或者 npm run build
```
打包产物将输出在 `admin/dist` 目录下，可直接使用 Nginx 进行静态资源代理部署。

---

## 🔗 相关模块
- 返回主项目：**[Z-Notify (主 README)](file:///Users/zxlee/Documents/GitHub/z-notify-api/README.md)**
- 后端服务接口：**[server/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/server/README.md)**
- H5 客户端/反馈页：**[public/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/public/README.md)**
