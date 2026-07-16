import App from './App'
import uView from '@/uni_modules/uview-ui'
import api from './api'
import utils from './utils'
import consts from './const'
import cache from './utils/cache.js'

// #ifndef VUE3
import Vue from 'vue'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
    ...App
})

require('@/config/request.js')(app)
Vue.prototype.$api = api
Vue.prototype.$utils = utils
Vue.prototype.$consts = consts
Vue.prototype.$cache = cache
app.$mount()

// #endif
Vue.use(uView)
// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif