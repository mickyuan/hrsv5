import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  withCredentials: true, // send cookies when cross-domain requests
  // Access-Control-Allow-Credentials : true,
  timeout: 5000 // request timeout
})

// request interceptor
// 请求拦截器
service.interceptors.request.use(
  config => {        
    // 每次发送请求之前判断vuex中是否存在token        
    // 如果存在，则统一在http请求的header都加上token，这样后台根据token判断你的登录情况
    // 即使本地存在token，也有可能token是过期的，所以在响应拦截器中要对返回状态进行判断 
    const token = store.state.token;
    if(token) {
      config.headers.Authorization = getToken();
    }
    
    return config;
},    
error => {        
    return Promise.error(error);    
})

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    // console.log(res)
    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== 200) {

      // TODO: 这里的代码需要重构 start


      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
        MessageBox.confirm('您已注销，可以取消以保留在该页面上，或者再次登录', '确认登出', {
          confirmButtonText: '重新登入',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$router.push('login')
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      else if (res.code == 500) {//如果返回的状态是 500表示服务器异常
        Message({
          message: '服务器异常',
          type: 'error',
          duration: 5 * 1000
        })
      }
      else {
        Message({
          message: res.message,
          type: 'error',
          duration: 5 * 1000
        })
      }
      // return null;
      // return Promise.reject(new Error(res.message || 'Error'))
      // TODO: 这里的代码需要重构 end
    } else {

      return res
    }
  },
  error => {
    // console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service