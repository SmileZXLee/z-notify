# Z-Notify - 后端服务 (server)

<p align="center">
    <img alt="logo" src="https://admin.z-notify.zxlee.cn/logo.png" width="100" height="100" style="margin-bottom: 20px;">
</p>

`server` 目录是 `Z-Notify` 平台的后端 API 服务模块。基于 Spring Boot 2.x、MySQL 和 Redis 搭建，为管理后台与公共 H5 反馈页提供接口支持。

## 🛠️ 技术栈
* **核心框架**：Spring Boot 2.x
* **连接池与监控**：Druid 1.x
* **数据库**：MySQL 8.x + MyBatis ORM
* **缓存与校验**：Redis + Spring Validation
* **邮件服务**：Spring Mail + Thymeleaf 邮箱验证码模板
* **文件存储**：阿里云 OSS SDK 
* **接口文档**：Swagger 3.0 (Springfox)

---

## 🚀 运行与部署指南

### 1. 准备工作
在启动后端服务前，请先准备好以下基础环境：
- **JDK 1.8+**
- **Maven 3.6+**
- **MySQL 8.x**（需创建数据库并导入结构）
- **Redis 6.x**

### 2. 初始化数据库
1. 创建名称为 `z-notify` 的数据库（推荐使用 `utf8mb4` 字符集）。
2. 执行 **[db/ddl.sql](file:///Users/zxlee/Documents/GitHub/z-notify-api/server/db/ddl.sql)** 结构脚本，初始化所需的数据表。

### 3. 修改配置文件
打开 **[src/main/resources/application.properties](file:///Users/zxlee/Documents/GitHub/z-notify-api/server/src/main/resources/application.properties)**，修改以下配置参数：
- 数据库连接信息 (`spring.datasource.url`、`username`、`password`)
- Redis 主机与密码 (`spring.redis.host`、`port`、`password`)
- 邮箱发信配置 (`spring.mail.username`、`password` 等，使用 163 或其他 SMTP 授权码)
- 阿里云 OSS 密钥配置（用于用户反馈图片上传）

### 4. 运行服务
在 `server` 根目录下执行以下命令启动项目：

```bash
# 启动服务
./mvnw spring-boot:run
```
或者使用 Maven 打包：
```bash
# 清理并打成 jar 包（产物在 target 目录）
./mvnw clean package -Dmaven.test.skip=true
```

启动成功后，可在浏览器访问：
- **Swagger API 文档**：`http://localhost:8901/swagger-ui/index.html`

---

## 🔗 相关模块
- 返回主项目：**[Z-Notify (主 README)](file:///Users/zxlee/Documents/GitHub/z-notify-api/README.md)**
- 后台管理系统：**[admin/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin/README.md)**
- H5 客户端/反馈页：**[public/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/public/README.md)**
