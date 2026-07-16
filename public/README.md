# z-notify-public

<p align="center">
    <img alt="logo" src="https://admin.z-notify.zxlee.cn/logo.png" width="120" height="120" style="margin-bottom: 50px;">
</p>
<h1 align="center">z-notify-public</h1>
<h5 align="center">一个开源的应用统一在线管理平台(移动端h5)</h5>

## 链接

* 后台管理页面地址：[https://admin.z-notify.zxlee.cn](https://admin.z-notify.zxlee.cn)

* 后台管理源码地址：[https://github.com/SmileZXLee/z-notify-admin-vue](https://github.com/SmileZXLee/z-notify-admin-vue)

* API文档地址：[https://api.z-notify.zxlee.cn/swagger-ui/index.html](https://api.z-notify.zxlee.cn/swagger-ui/index.html)

* API源码地址：[https://github.com/SmileZXLee/z-notify-api](https://github.com/SmileZXLee/z-notify-api)

## 说明
### 反馈页配置
页面地址：[https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback](https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback)  
参数说明：
| project_id   | username                       | extra_info                                                   | show_footer                    |
| ------------ | ------------------------------ | ------------------------------------------------------------ | ------------------------------ |
| 项目id，必填 | 用户名，若不传则由用户自行填写 | 附加信息，如设备信息等。会一起提交给服务端，但不会显示在用户反馈页面上 | 是否显示底部版权信息，默认为是 |  

将参数拼接在url后方，如：https://h5.z-notify.zxlee.cn/#/pages/feedback/feedback?project_id=123&username=zxlee

