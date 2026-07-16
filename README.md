# Z-Notify

<p align="center">
    <img alt="logo" src="https://admin.z-notify.zxlee.cn/logo.png" width="120" height="120" style="margin-bottom: 20px;">
</p>
<h1 align="center">Z-Notify</h1>
<h5 align="center">一个开源的应用统一在线管理平台，支持多端部署与极简对接</h5>

---

## 📌 项目简介

`Z-Notify` 是一个轻量级、开箱即用的应用在线管理平台。项目采用 Monorepo（多模块单仓库）结构，整合了后端服务、管理后台以及面向终端用户的公共 H5 页面。它可以帮助开发者快速实现软件版本迭代管理、公告发布、自定义文本下发、用户意见反馈、以及网页/应用流量统计等功能。

### 📁 项目结构说明

- **[server](file:///Users/zxlee/Documents/GitHub/z-notify-api/server)**：后端 API 服务，基于 Spring Boot、MySQL 与 Redis 开发。
- **[admin](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin)**：后台管理系统，基于 Vue 2 与 Element UI 构建。
- **[public](file:///Users/zxlee/Documents/GitHub/z-notify-api/public)**：公共 H5 页面模块（如用户意见反馈页），基于 uni-app 开发，支持跨端编译。

---

## 🔗 相关链接

* **在线后台管理演示**：[https://admin.z-notify.zxlee.cn](https://admin.z-notify.zxlee.cn)
* **在线反馈页演示**：[https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback](https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback)
* **Swagger API 文档**：[https://api.z-notify.zxlee.cn/swagger-ui/index.html](https://api.z-notify.zxlee.cn/swagger-ui/index.html)

---

## ⚙️ 主要功能

1. **版本管理**：公共接口快速返回最新版本号、更新日志、下载链接，轻松对接客户端自动更新。
2. **通知管理**：后台发布通知、公告，支持设置过期时间，接口只返回未过期的有效内容。
3. **通用文本配置**：支持自定义任意 Key-Value 对（如公告、配置开关），实现客户端的动态云控制。
4. **反馈管理**：提供开箱即用的移动端 H5 反馈提交页，支持用户上传多图，管理员可在后台回复，用户能实时查看回复进度。
5. **用户流量统计与分析**：支持生成 SVG Badge 访问计数徽章直接嵌入 Markdown 或网页中，并可在后台直观查看多维度访客 IP 归属地与流量趋势分析。
   - 例如：`![visitors](https://api.z-notify.zxlee.cn/v1/public/statistics/YOUR_PROJECT_ID/badge)`

---

## 📸 界面预览

#### 项目列表
[![overview](https://admin.z-notify.zxlee.cn/public/overview.png)](https://admin.z-notify.zxlee.cn)

#### 统计分析
[![overview](https://admin.z-notify.zxlee.cn/public/demo-analysis-2.png)](https://admin.z-notify.zxlee.cn)

---

## 🛠️ 技术栈

### 后端服务 (`server`)
* **核心框架**：Spring Boot 2.x
* **数据存储**：MySQL 8.x + Redis
* **ORM 框架**：MyBatis + Druid 连接池
* **接口文档**：Swagger 3.0 (Springfox)
* **其它特性**：雪花算法 ID 生成、Spring Validation 参数校验、Thymeleaf 邮件模版、阿里云 OSS 多文件上传。

### 管理后台 (`admin`)
* **核心框架**：Vue 2.x + Vue Router + Vuex
* **UI 组件库**：Element UI
* **网络请求**：Axios
* **构建工具**：Vue CLI

### 公共客户端 (`public`)
* **核心框架**：uni-app + Vue
* **网络请求**：@escook/request-miniprogram
* **样式处理**：SCSS / Sass

---

## 🚀 快速开始

请分别进入各模块目录查看详细的运行指南：

* 运行后端服务：参考 **[server/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/server/README.md)**
* 运行管理后台：参考 **[admin/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin/README.md)**
* 运行 H5 公共页：参考 **[public/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/public/README.md)**
