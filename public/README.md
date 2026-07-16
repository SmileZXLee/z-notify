# Z-Notify - 公共反馈页 (public)

<p align="center">
    <img alt="logo" src="https://admin.z-notify.zxlee.cn/logo.png" width="100" height="100" style="margin-bottom: 20px;">
</p>

`public` 目录是 `Z-Notify` 平台的公共客户端模块（主要包含移动端 H5 意见反馈页）。基于 **uni-app** 构建，主要用于嵌入到其他 App、微信小程序或网页中，方便收集用户的反馈意见。

## ⚙️ 页面参数说明

**反馈页地址**：`https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback`  
你可以直接通过在 URL 后面拼接 Query 参数来定制反馈页面：

| 参数名 | 必填 | 说明 | 示例 |
| :--- | :--- | :--- | :--- |
| **`project_id`** | **是** | 在管理后台创建的项目 ID，系统会根据此 ID 归档反馈内容 | `project_id=8299976976587751424` |
| **`username`** | 否 | 用户的标识。若不传，页面将允许用户在提交时自行输入用户名 | `username=zxlee` |
| **`extra_info`** | 否 | 附加的设备信息或运行环境上下文（如应用版本号等）。提交后管理员可在后台查看，但不会展示在用户的反馈页面上 | `extra_info=iOS15_v1.0.2` |
| **`show_footer`** | 否 | 是否显示底部的版权申明，默认为 `true`。传入 `false` 可隐藏底部版权 | `show_footer=false` |

**完整拼接示例**：  
`https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback?project_id=123&username=zxlee&extra_info=AppVersion_1.0&show_footer=false`

---

## 🚀 运行与编译指南

由于本模块是标准的 **uni-app** 目录结构，推荐使用 **HBuilderX** 编辑器进行可视化运行与打包：

### 1. 导入项目
1. 下载并安装官方开发工具 [HBuilderX](https://www.dcloud.io/hbuilderx.html)。
2. 在工具栏选择 `文件` -> `导入` -> `从本地目录导入`，并选中该 `public` 文件夹。

### 2. 接口地址配置
你可以修改 **[config/index.js](file:///Users/zxlee/Documents/GitHub/z-notify-api/public/config/index.js)** 文件来修改对接的后端服务器 API 地址：
- **开发环境 (`development`)**：默认为 `http://localhost:8900`
- **生产环境 (`production`)**：默认为 `https://api.z-notify.zxlee.cn`

### 3. 本地开发调试
- 在 HBuilderX 顶部工具栏点击 `运行` -> `运行到浏览器` -> 选择你的浏览器（如 Chrome）。
- 浏览器会自动打开本地 H5 调试页面。

### 4. 生产环境打包
- 在 HBuilderX 顶部工具栏点击 `发行` -> `网站-PC Web或手机H5`。
- 填写网站标题与路由配置，点击 `发行` 按钮。
- 打包成功后，编译产物将输出在 `unpackage/dist/build/h5` 目录下，直接将其上传到静态托管服务器（如 Nginx）即可。

---

## 🔗 相关模块
- 返回主项目：**[Z-Notify (主 README)](file:///Users/zxlee/Documents/GitHub/z-notify-api/README.md)**
- 后端服务接口：**[server/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/server/README.md)**
- 后台管理系统：**[admin/README.md](file:///Users/zxlee/Documents/GitHub/z-notify-api/admin/README.md)**
