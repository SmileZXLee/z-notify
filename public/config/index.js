let BASE_URL = '';

if (process.env.NODE_ENV === 'development') {
  BASE_URL = 'http://localhost:8900' //dev环境接口
} else {
  BASE_URL = 'https://api.z-notify.zxlee.cn' //正式生产环境
}

module.exports = {
	BASE_URL
};