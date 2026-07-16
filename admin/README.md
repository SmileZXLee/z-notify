# z-notify-admin-vue

<p align="center">
    <img alt="logo" src="https://admin.z-notify.zxlee.cn/logo.png" width="120" height="120" style="margin-bottom: 50px;">
</p>
<h1 align="center">z-notify-admin-vue</h1>
<h5 align="center">一个开源的应用统一在线管理平台(web)</h5>

## 链接

* 后台管理页面地址：[https://admin.z-notify.zxlee.cn](https://admin.z-notify.zxlee.cn)

* 公共页(反馈页)源码地址：[https://github.com/SmileZXLee/z-notify-public](https://github.com/SmileZXLee/z-notify-public)

* API文档地址：[https://api.z-notify.zxlee.cn/swagger-ui/index.html](https://api.z-notify.zxlee.cn/swagger-ui/index.html)

* API源码地址：[https://github.com/SmileZXLee/z-notify-api](https://github.com/SmileZXLee/z-notify-api)


## 主要功能
* 版本管理，通过公共接口返回所有新版本号、更新内容、下载地址

* 通知管理，支持发布通知和设置过期时间，通过公共接口获取所有未过期的通知

* 通用文本管理，可以随意自定义key并通过公共接口获取key对应的文本

* 反馈管理，提供用户反馈提交页，用户提交后可在管理后台查看并回复，用户可在反馈页查看开发者回复的内容

* 用户流量统计&分析，支持生成`badge`嵌入网页统计访问次数，支持根据ip统计访问人数等，流量分析可在管理后台查看。例如：在网页或md中插入`![visitors](https://api.z-notify.zxlee.cn/v1/public/statistics/8299976976587751424/badge)`

预览
----
#### 项目列表
[![overview](https://admin.z-notify.zxlee.cn/public/overview.png)](https://admin.z-notify.zxlee.cn)
#### 统计分析
[![overview](https://admin.z-notify.zxlee.cn/public/demo-analysis-2.png)](https://admin.z-notify.zxlee.cn)
 
项目下载和运行
----

- 拉取项目代码
```bash
git clone https://github.com/SmileZXLee/z-notify-admin-vue.git
cd z-notify-admin-vue
```

- 安装依赖
```
yarn install
```

- 开发模式运行
```
yarn run serve
```

- 编译项目
```
yarn run build
```

- Lints and fixes files
```
yarn run lint
```

## 浏览器兼容

Modern browsers and IE10.

| [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/edge/edge_48x48.png" alt="IE / Edge" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>IE / Edge | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/firefox/firefox_48x48.png" alt="Firefox" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Firefox | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/chrome/chrome_48x48.png" alt="Chrome" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Chrome | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/safari/safari_48x48.png" alt="Safari" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Safari | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/opera/opera_48x48.png" alt="Opera" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Opera |
| --- | --- | --- | --- | --- |
| IE10, Edge | last 2 versions | last 2 versions | last 2 versions | last 2 versions |

## 其他功能
正在开发...
