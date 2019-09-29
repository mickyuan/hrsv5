import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/A/action/hrds/a/biz/login/login',
    method: 'post',
    params : data
  })
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function getLoginTokern() {
    return request({
      url : '/A/action/hrds/a/biz/login/getToken',
      method : 'post'
    })
}