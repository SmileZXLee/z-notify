import commonConfig from './index.js'
import consts from '../const'
module.exports = (vm) => {
    // 初始化请求配置
    uni.$u.http.setConfig((config) => {
        /* config 为默认全局配置*/
        config.baseURL = commonConfig.BASE_URL; /* 根域名 */
        return config;
    })
	
	// 请求拦截
	uni.$u.http.interceptors.request.use((config) => { // 可使用async await 做异步操作
		uni.showLoading({
			title: '加载中...'
		})
	    // 初始化请求拦截器时，会执行此方法，此时data为undefined，赋予默认{}
	    config.data = config.data || {};
		const token = uni.getStorageSync(consts.TOKEN_STORAGE_KEY);
		if (!!token) {
			config.header.token = token;
		}
	    return config;
	}, config => { // 可使用async await 做异步操作
	    return Promise.reject(config);
	})
	
	// 响应拦截
	uni.$u.http.interceptors.response.use((response) => { /* 对响应成功做点什么 可使用async await 做异步操作*/
		uni.hideLoading();
		const data = response.data;
		// 自定义参数
		const custom = response.config?.custom
		if (data.status !== 0) { 
			// 如果没有显式定义custom的toast参数为false的话，默认对报错进行toast弹出提示
			if (custom.toast !== false) {
				uni.$u.toast(data.message)
			}
			if (data.status === 10002) {
				// 未登录或登录过期
				uni.removeStorageSync(consts.TOKEN_STORAGE_KEY);
				uni.redirectTo({
					url: '/pages/login/login'
				})
			}
			return Promise.reject(data)
		}
		return data;
	}, error => {
		uni.hideLoading();
		uni.$u.toast('请求异常');
		return Promise.reject();
	})
}