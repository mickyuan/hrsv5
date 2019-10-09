import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/A/action/hrds/a/biz/login/login',
    method: 'post',
    params : data
  })
}

export function getInfo(token) {
  return request({
    url: '/api/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/api/user/logout',
    method: 'post'
  })
}

export function getLoginTokern() {
    return request({
      url: '/api/A/action/hrds/a/biz/login/getToken',
      method : 'post'
    })
}